package com.github.sputnik1111.service.product.domain.product.rest;

import com.github.sputnik1111.api.product.ProductDto;
import com.github.sputnik1111.common.domain.page.Page;
import com.github.sputnik1111.service.product.domain.product.CreateProductDto;
import com.github.sputnik1111.service.product.domain.product.ProductMapper;
import com.github.sputnik1111.service.product.domain.product.ProductView;
import com.github.sputnik1111.service.product.domain.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final UserService userService;

    public ProductController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<ProductDto> getProducts(@RequestHeader("USERID") Long userId) {
        return new Page<>(ProductMapper.fromProductView(userService.findByProductByUserId(userId),userId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductView> create(@RequestHeader("USERID") Long userId, @RequestBody CreateProductDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addProduct(userId, request));
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<ProductView> create(@PathVariable Long productId) {
        return userService.deleteProduct(productId)
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
