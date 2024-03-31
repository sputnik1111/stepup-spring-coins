package com.github.sputnik1111.service.userpay.domain.userpay;

import com.github.sputnik1111.service.userpay.common.jobrun.JobRunService;
import com.github.sputnik1111.service.userpay.domain.payapply.PayApplyRequestDto;
import com.github.sputnik1111.service.userpay.domain.payapply.PayApplyService;
import com.github.sputnik1111.service.userpay.domain.payapply.PayApplyStatus;
import com.github.sputnik1111.service.userpay.domain.payconfirm.PayConfirmRepository;
import com.github.sputnik1111.service.userpay.domain.payconfirm.PayConfirmView;
import com.github.sputnik1111.service.userpay.domain.userlimit.UserLimitIncreaseDto;
import com.github.sputnik1111.service.userpay.domain.userlimit.UserLimitRepository;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UserPayService {

    private static final String RESET_LIMIT_JOB_NAME = "resetLimit";

    private final UserLimitRepository userLimitRepository;

    private final PayConfirmRepository payConfirmRepository;

    private final PayApplyService payApplyService;

    private final UserPayProperties userPayProperties;

    private final JobRunService jobRunService;

    private final TransactionTemplate transactionTemplate;
    private final ScheduledExecutorService checkPayConfirmExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> checkPayConfirmScheduledFuture;

    @EventListener
    void listen(ApplicationReadyEvent event) {
        UserPayProperties.Cron resetLimitCron = userPayProperties.getResetLimitCron();
        if (resetLimitCron!=null){
            jobRunService.scheduleCron(
                    RESET_LIMIT_JOB_NAME,
                    resetLimitCron.getCron(),
                    resetLimitCron.getZone(),
                    this::resetLimits
            );
        }

        this.checkPayConfirmScheduledFuture = checkPayConfirmExecutorService.scheduleWithFixedDelay(
                this::checkPayConfirm,
                0,
                userPayProperties.getCheckPayConfirmDelayInMs(),
                TimeUnit.MILLISECONDS
        );

    }

    public Optional<Long> currentLimit(long userId){
        return userLimitRepository.currentLimitForUser(userId);
    }

    @Transactional
    public UUID executePay(long userId, long amount){
        if (amount<1)
            throw new IllegalArgumentException("amount must be greater than 0");

        boolean success = userLimitRepository.upsertCurrentLimitByUserId(
                userId,
                userPayProperties.getInitLimit(),
                amount
        );

        if (!success)
            throw new IllegalArgumentException("not enough coins");

        PayConfirmView payConfirmView = payConfirmRepository.insert(userId,amount);

        if (checkPayConfirmScheduledFuture.getDelay(TimeUnit.MILLISECONDS)>1000)
            checkPayConfirmExecutorService.submit(this::checkPayConfirm);

        return payConfirmView.payId();
    }

    public void checkPayConfirm(){

        final AtomicLong startIdExclude = new AtomicLong( -1 ) ;

        Boolean hasMore;

        do{
            hasMore = transactionTemplate.execute(status -> {
                List<PayConfirmView> payConfirmViews =
                        payConfirmRepository.findAllOrderByIdForUpdateSkipLocked(
                                userPayProperties.getCheckPayConfirmBatch(),
                                startIdExclude.get()
                        );
                if (payConfirmViews.isEmpty()) return false;

                List<PayApplyRequestDto> payRequests = payConfirmViews.stream()
                        .map(v->new PayApplyRequestDto(v.payId(),v.userId(),v.subLimit()))
                        .toList();

                List<PayApplyStatus> payApplyStatuses = payApplyService.applyPays(payRequests);

                Set<UUID> deleteConfirmIds = new HashSet<>();
                Set<UserLimitIncreaseDto> increaseDtos = new HashSet<>();

                for(int i = 0; i< payApplyStatuses.size(); i++){
                    switch (payApplyStatuses.get(i)){
                        case COMPLETED -> deleteConfirmIds.add(payConfirmViews.get(i).payId());
                        case REJECTED -> {
                            deleteConfirmIds.add(
                                    payConfirmViews.get(i).payId()
                            );
                            increaseDtos.add(from(
                                    payConfirmViews.get(i),
                                    userPayProperties.getInitLimit()
                            ));
                        }
                    }
                }

                payConfirmRepository.deleteAllByPayIds(deleteConfirmIds);

                userLimitRepository.increaseLimits(increaseDtos);

                startIdExclude.set(payConfirmViews.get(payConfirmViews.size()-1).id());

                return payConfirmViews.size()==userPayProperties.getCheckPayConfirmBatch();
            });

        }while (Boolean.TRUE.equals(hasMore));


    }

    private static UserLimitIncreaseDto from(@NonNull PayConfirmView v, long maxLimit){
        return new UserLimitIncreaseDto(
                v.userId(),
                v.subLimit(),
                maxLimit
        );
    }


    private void resetLimits(){
        transactionTemplate.executeWithoutResult(s->{
            userLimitRepository.restoreLimitForAllUsers(userPayProperties.getInitLimit());
            payConfirmRepository.deleteAll();
        });
    }

    @PreDestroy
    public void destroy() {
        checkPayConfirmExecutorService.shutdownNow();
    }
}
