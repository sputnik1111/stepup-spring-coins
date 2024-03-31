package com.github.sputnik1111.service.userpay.domain.payapply;

import lombok.NonNull;

import java.util.List;

public interface PayApplyService {

    /**
     * Сервис проведения платежей. Принимает список платежей. Если такого платежа еще не передавалось (payId) то создает его
     * если он уже был то возвращает его статус
     * @param requests - список платежей
     * @return статусы платежей по каждому переданному платежу
     */
    List<PayApplyStatus> applyPays(@NonNull List<PayApplyRequestDto> requests);

}
