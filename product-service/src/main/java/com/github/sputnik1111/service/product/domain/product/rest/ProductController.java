package com.github.sputnik1111.service.product.domain.product.rest;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.service.product.domain.product.ProductMapper;
import com.github.sputnik1111.service.product.domain.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final UserService userService;

    public ProductController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<ProductDto> create(@RequestParam Long userId) {
        return ProductMapper.fromProductView(userService.findByProductByUserId(userId),userId);
    }
}
