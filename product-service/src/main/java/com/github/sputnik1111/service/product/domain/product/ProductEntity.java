package com.github.sputnik1111.service.product.domain.product;

public class ProductEntity {
    private Long id;

    private Long userId;

    private String account;

    private Long balance;

    private TypeProduct typeProduct;

    public ProductEntity(Long id, long userId, String account, long balance, TypeProduct typeProduct) {

        if (account == null)
            throw new IllegalArgumentException(" account is null ");

        if (typeProduct == null)
            throw new IllegalArgumentException(" typeProduct is null ");

        this.id = id;
        this.userId = userId;
        this.account = account;
        this.balance = balance;
        this.typeProduct = typeProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
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

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", userId=" + userId +
                ", account='" + account + '\'' +
                ", balance=" + balance +
                ", typeProduct=" + typeProduct +
                '}';
    }
}
