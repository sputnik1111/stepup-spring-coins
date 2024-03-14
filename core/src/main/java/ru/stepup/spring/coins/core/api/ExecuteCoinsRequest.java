package ru.stepup.spring.coins.core.api;

public record ExecuteCoinsRequest(
        String number,
        String productId,
        String productType,
        Long summ
) {
}
