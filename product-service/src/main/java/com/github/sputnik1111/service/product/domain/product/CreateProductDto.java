package com.github.sputnik1111.service.product.domain.product;

public class CreateProductDto {
    private final String account;

    private final Long balance;

    private final TypeProduct typeProduct;

    public CreateProductDto(String account, Long balance, TypeProduct typeProduct) {
        if (account == null || account.isBlank())
            throw new IllegalArgumentException("account is null or blank");

        if (balance == null)
            throw new IllegalArgumentException("balance is null");

        if (typeProduct == null)
            throw new IllegalArgumentException("typeProduct is null");

        this.account = account;
        this.balance = balance;
        this.typeProduct = typeProduct;
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
