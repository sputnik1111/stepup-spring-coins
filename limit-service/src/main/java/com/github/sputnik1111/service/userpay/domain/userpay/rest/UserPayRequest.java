package com.github.sputnik1111.service.userpay.domain.userpay.rest;

import jakarta.validation.constraints.Positive;

public record UserPayRequest(
        @Positive
        long amount
) {}
