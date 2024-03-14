package ru.stepup.spring.coins.core.controllers;

import com.github.sputnik1111.api.product.ProductDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.stepup.spring.coins.core.dtos.PageDto;
import ru.stepup.spring.coins.core.services.ProductService;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageDto<ProductDto> create(@RequestParam Long userId) {
        return new PageDto<>(productService.findByUserId(userId));
    }
}
