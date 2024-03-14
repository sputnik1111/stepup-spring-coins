package ru.stepup.spring.coins.core.services;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.api.product.ProductIntegration;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductIntegration productIntegration;

    public ProductService(ProductIntegration productIntegration) {
        this.productIntegration = productIntegration;
    }

    public List<ProductDto> findByUserId(Long userId){
        return productIntegration.findByUserId(userId);
    }

    public Optional<ProductDto> findByUserIdAndProductId(Long userId, String productId){
        List<ProductDto> products = findByUserId(userId);

        return products.stream()
                .filter(p->p.id()!=null)
                .filter(p->p.id().toString().equals(productId))
                .findFirst();

    }
}
