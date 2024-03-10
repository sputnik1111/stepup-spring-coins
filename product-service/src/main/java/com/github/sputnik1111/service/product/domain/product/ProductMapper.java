package com.github.sputnik1111.service.product.domain.product;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.api.product.TypeProduct;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    private ProductMapper() {
    }

    public static ProductDto from(ProductView v,Long userId){
        if (v==null) return null;
        return new ProductDto(
            v.getId(),
            userId,
            v.getAccount(),
            v.getBalance(),
            TypeProduct.valueOf(v.getTypeProduct().name())
        );
    }

    public static List<ProductDto> fromProductView(Collection<ProductView> productViews,Long userId){
        if (productViews==null) return null;
        return productViews.stream()
                .map(v->from(v,userId))
                .collect(Collectors.toList());
    }
}
