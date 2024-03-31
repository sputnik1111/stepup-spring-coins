package com.github.sputnik1111.service.userpay.domain.payconfirm;


import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@FieldNameConstants
public record PayConfirmView(
        @NonNull Long id,
        @NonNull UUID payId,
        long userId,
        long subLimit
) {}
