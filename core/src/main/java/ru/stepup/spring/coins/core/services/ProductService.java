package ru.stepup.spring.coins.core.services;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.api.product.ProductIntegration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductIntegration productIntegration;

    public ProductService(ProductIntegration productIntegration) {
        this.productIntegration = productIntegration;
    }

    public List<ProductDto> findByUserId(Long userId){
        return productIntegration.findByUserId(userId);
    }
}
