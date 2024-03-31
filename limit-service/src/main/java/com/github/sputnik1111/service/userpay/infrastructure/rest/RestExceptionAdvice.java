package com.github.sputnik1111.service.userpay.infrastructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(400).body(ErrorDto.from(e));
    }

}
