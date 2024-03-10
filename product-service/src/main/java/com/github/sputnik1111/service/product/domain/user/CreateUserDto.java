package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.domain.product.CreateProductDto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CreateUserDto {
    private final String username;

    private final Set<CreateProductDto> products;

    public CreateUserDto(String username, Set<CreateProductDto> products) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("username is null or blank");
        this.username = username;
        this.products = Collections.unmodifiableSet(products != null
                ? products
                : new HashSet<>()
        );
    }

    public String getUsername() {
        return username;
    }

    public Set<CreateProductDto> getProducts() {
        return products;
    }


}
