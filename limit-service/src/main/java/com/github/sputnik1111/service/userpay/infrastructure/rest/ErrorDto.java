package com.github.sputnik1111.service.userpay.infrastructure.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDto {

    @NonNull
    String code;

    String message;

    public static ErrorDto from(@NonNull IllegalArgumentException e){
        return new ErrorDto(
                "IllegalArgument",
                e.getMessage()
        );
    }
}
