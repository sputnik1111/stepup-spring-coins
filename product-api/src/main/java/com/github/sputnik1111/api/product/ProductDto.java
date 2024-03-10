package com.github.sputnik1111.api.product;

public record ProductDto(
        Long id,

        Long userId,

        String account,
        Long balance,
        TypeProduct typeProduct
) {
}
