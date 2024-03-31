package com.github.sputnik1111.service.userpay.domain.payapply;

import lombok.NonNull;

import java.util.UUID;

public record PayApplyRequestDto(
        @NonNull UUID payId,

        long userId,

        long amount
) {}
