package com.github.sputnik1111.service.userpay.domain.userpay;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;

@Data
@Configuration
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "user-pay")
public class UserPayProperties {

    @Positive
    long initLimit = 10000;

    long checkPayConfirmDelayInMs = 500;

    int checkPayConfirmBatch = 100;

    Cron resetLimitCron;

    @Data
    @Validated
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Cron{
        @NotEmpty
        String cron = "0 0 0 * * *";

        ZoneId zone = ZoneId.of("UTC");
    }
}
