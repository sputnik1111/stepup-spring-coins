package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.domain.product.ProductView;

import java.util.Set;

public class UserView {

    private final Long id;

    private final String username;

    private final Set<ProductView> products;

    public UserView(Long id, String username, Set<ProductView> products) {
        this.id = id;
        this.username = username;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<ProductView> getProducts() {
        return products;
    }
}
