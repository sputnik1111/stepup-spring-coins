package com.github.sputnik1111.service.userpay.domain.payapply;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayApplyServiceMockImpl implements PayApplyService {


    public List<PayApplyStatus> applyPays(@NonNull List<PayApplyRequestDto> requests){
        return requests.stream()
                .map(PayApplyServiceMockImpl::from)
                .toList();
    }

    private static PayApplyStatus from(@NonNull PayApplyRequestDto request){
        if (request.userId()<10) return PayApplyStatus.EXECUTING;
        if (request.userId()<20) return PayApplyStatus.REJECTED;
        return PayApplyStatus.COMPLETED;
    }

}
