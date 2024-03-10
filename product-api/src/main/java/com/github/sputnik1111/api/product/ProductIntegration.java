package com.github.sputnik1111.api.product;

import java.util.List;

public interface ProductIntegration {

    List<ProductDto> findByUserId(Long userId);
}
