package com.github.sputnik1111.service.userpay.domain.userpay.rest;

import com.github.sputnik1111.service.userpay.domain.userpay.UserPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public class UserPayController {

    private final UserPayService userPayService;

    @PostMapping("/execute")
    public UUID execute(
            @RequestBody UserPayRequest request,
            @RequestHeader("USERID") long userId
    ) {
        return userPayService.executePay(userId,request.amount());
    }

    @GetMapping("/limit")
    public ResponseEntity<Long> execute(
            @RequestHeader("USERID") long userId
    ) {
        return userPayService.currentLimit(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
