package com.github.sputnik1111.service.product.domain.product;

public class ProductView {

    private final Long id;

    private final String account;

    private final Long balance;

    private final TypeProduct typeProduct;

    public ProductView(Long id, String account, Long balance, TypeProduct typeProduct) {
        this.id = id;
        this.account = account;
        this.balance = balance;
        this.typeProduct = typeProduct;
    }

    public Long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public Long getBalance() {
        return balance;
    }

    public TypeProduct getTypeProduct() {
        return typeProduct;
    }
}
