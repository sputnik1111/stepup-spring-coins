package com.github.sputnik1111.service.userpay.common.jobrun;

import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Service
public class JobRunService {

    private final static TriggerContext EMPTY_TRIGGER_CONTEXT  = new EmptyTriggerContext();

    private final JobRunRepository jobRunRepository;

    private final TransactionTemplate transactionTemplate;

    private final Map<String, CronTrigger> jobNameToCronTrigger = new ConcurrentHashMap<>();

    private final Map<String, ScheduledExecutorService> jobNameToScheduler = new ConcurrentHashMap<>();

    public void scheduleCron(
            @NonNull String jobName,
            @NonNull String cron,
            @NonNull ZoneId zone,
            @NonNull Runnable runnable
    ){
        CronTrigger cronTrigger = new CronTrigger(
                cron,
                zone
        );

        CronTrigger existedCronTrigger = jobNameToCronTrigger.putIfAbsent(jobName,cronTrigger);
        if (existedCronTrigger!=null)
            throw new IllegalStateException("Job "+jobName+" has already scheduled");

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        jobNameToScheduler.put(jobName,scheduler);

        scheduler.schedule(
                ()->tryRunJob(jobName,runnable),
                0,
                TimeUnit.MILLISECONDS
        );

    }

    private void tryRunJob(@NonNull String jobName, @NonNull Runnable runnable){
        try{
            Instant nextRun = jobRunRepository.getNextRun(jobName)
                    .orElseGet(()->{
                        jobRunRepository.insertJob(jobName,nextRunByCron(jobName));
                        return jobRunRepository.getNextRun(jobName)
                                .orElseThrow();
                    });
            AtomicLong restWaitMills = new AtomicLong(nextRun.toEpochMilli()-Instant.now().toEpochMilli());
            if (restWaitMills.get()<=0){
                transactionTemplate.executeWithoutResult(s->{
                    Instant newNextRun = nextRunByCron(jobName);
                    if (nextRun.isAfter(newNextRun))
                        throw new IllegalStateException("invalid newNextRun");
                    if (jobRunRepository.updateNextRun(jobName,nextRun,newNextRun)){
                        runnable.run();
                        restWaitMills.set(newNextRun.toEpochMilli()-Instant.now().toEpochMilli());
                    }else{
                        restWaitMills.set(0);
                    }
                });
            }

            jobNameToScheduler.get(jobName).schedule(
                    ()->tryRunJob(jobName,runnable),
                    restWaitMills.get(),
                    TimeUnit.MILLISECONDS
            );
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private Instant nextRunByCron(@NonNull String jobName){
        Instant nextRun = jobNameToCronTrigger.get(jobName).nextExecution(EMPTY_TRIGGER_CONTEXT);
        if (nextRun==null)
            throw new IllegalStateException("nextRun==null");
        return nextRun;
    }

    @PreDestroy
    public void destroy() {
        jobNameToScheduler.values().forEach(ExecutorService::shutdownNow);
    }

    private static class EmptyTriggerContext implements TriggerContext{

        @Override
        public Instant lastScheduledExecution() {
            return null;
        }

        @Override
        public Instant lastActualExecution() {
            return null;
        }

        @Override
        public Instant lastCompletion() {
            return null;
        }
    }
}
