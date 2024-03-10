package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.domain.product.CreateProductDto;
import com.github.sputnik1111.service.product.domain.product.ProductEntity;
import com.github.sputnik1111.service.product.domain.product.ProductView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserView from(UserEntity userEntity, List<ProductEntity> products) {
        if (userEntity == null) return null;
        return new UserView(
                userEntity.getId(),
                userEntity.getUsername(),
                Optional.ofNullable(products)
                        .orElse(new ArrayList<>())
                        .stream()
                        .map(UserMapper::from)
                        .collect(Collectors.toSet())

        );
    }

    public static ProductView from(ProductEntity productEntity) {
        if (productEntity == null) return null;
        return new ProductView(
                productEntity.getId(),
                productEntity.getAccount(),
                productEntity.getBalance(),
                productEntity.getTypeProduct()
        );
    }

    public static List<ProductView> fromProductEntity(List<ProductEntity> productEntities) {
        return productEntities.stream()
                .map(UserMapper::from)
                .collect(Collectors.toList());
    }

    public static ProductEntity from(Long userId, CreateProductDto createProductDto) {
        return new ProductEntity(
                null,
                userId,
                createProductDto.getAccount(),
                createProductDto.getBalance(),
                createProductDto.getTypeProduct()
        );
    }
}
