package com.github.sputnik1111.service.userpay.domain.userlimit;

public record UserLimitIncreaseDto(
        long userId,
        long amount,
        long maxLimit
) {}
