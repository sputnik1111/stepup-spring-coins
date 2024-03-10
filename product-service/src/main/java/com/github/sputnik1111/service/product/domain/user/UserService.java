package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.domain.product.CreateProductDto;
import com.github.sputnik1111.service.product.domain.product.ProductDao;
import com.github.sputnik1111.service.product.domain.product.ProductEntity;
import com.github.sputnik1111.service.product.domain.product.ProductView;
import com.github.sputnik1111.service.product.infrastructure.transaction.TransactionTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;

    private final ProductDao productDao;

    private final TransactionTemplate transactionTemplate;

    public UserService(
            UserDao userDao,
            ProductDao productDao,
            TransactionTemplate transactionTemplate
    ) {
        this.userDao = userDao;
        this.productDao = productDao;
        this.transactionTemplate = transactionTemplate;
    }

    public UserView create(CreateUserDto request) {
        return transactionTemplate.inTransaction(() -> {
            Long userId = userDao.insert(request.getUsername());
            Set<ProductView> products = request.getProducts().stream()
                    .map(product -> {
                        Long productId = productDao.insert(new ProductEntity(
                                null,
                                userId,
                                product.getAccount(),
                                product.getBalance(),
                                product.getTypeProduct()
                        ));
                        return new ProductView(
                                productId,
                                product.getAccount(),
                                product.getBalance(),
                                product.getTypeProduct()
                        );
                    }).collect(Collectors.toSet());
            return new UserView(
                    userId,
                    request.getUsername(),
                    products
            );
        });
    }

    public boolean updateUsername(Long userId, String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("username is null or blank");
        return userDao.update(userId, username);
    }

    public boolean delete(Long userId) {
        return userDao.delete(userId);
    }

    public Optional<UserView> findById(Long userId) {
        return userDao.findById(userId)
                .map(userEntity -> UserMapper.from(
                                userEntity,
                                productDao.findByUserId(userEntity.getId())
                        )
                );
    }

    public List<UserView> findAll() {
        List<UserEntity> users = userDao.findAll();
        Set<Long> userIds = users.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        Map<Long, List<ProductEntity>> productEntitiesGroupByUserId = productDao.findByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(ProductEntity::getUserId));

        return users.stream().map(userEntity -> UserMapper.from(
                userEntity,
                productEntitiesGroupByUserId.getOrDefault(userEntity.getId(), Collections.emptyList())
        )).collect(Collectors.toList());
    }

    public ProductView addProduct(Long userId, CreateProductDto createProductDto) {
        ProductEntity productEntity = UserMapper.from(userId, createProductDto);
        Long productId = productDao.insert(productEntity);
        productEntity.setId(productId);
        return UserMapper.from(productEntity);
    }

    public boolean deleteProduct(Long productId) {
        return productDao.delete(productId);
    }

    public List<ProductView> findByProductByUserId(Long userId) {
        return UserMapper.fromProductEntity(productDao.findByUserId(userId));
    }

    public Optional<ProductEntity> findByProductId(Long productId) {
        return productDao.findById(productId);
    }

    public void clear() {
        userDao.clear();
    }
}
