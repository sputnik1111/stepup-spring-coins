package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.domain.product.CreateProductDto;
import com.github.sputnik1111.service.product.domain.product.ProductEntity;
import com.github.sputnik1111.service.product.domain.product.ProductRepository;
import com.github.sputnik1111.service.product.domain.product.ProductView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Transactional
    public UserView create(CreateUserDto request) {
        UserEntity createdUser = userRepository.save(UserEntity.newUser(request.getUsername()));

        List<ProductEntity> productEntities = request.getProducts().stream()
                .map(product->ProductEntity.newProduct(
                        createdUser,
                        product.getAccount(),
                        product.getBalance(),
                        product.getTypeProduct()
                )).toList();

        productEntities =  productRepository.saveAll(productEntities);

        Set<ProductView> products = productEntities.stream()
                .map(product -> new ProductView(
                        product.getId(),
                        product.getAccount(),
                        product.getBalance(),
                        product.getTypeProduct()
                )).collect(Collectors.toSet());

        return new UserView(
                createdUser.getId(),
                request.getUsername(),
                products
        );
    }

    @Transactional
    public boolean updateUsername(Long userId, String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("username is null or blank");
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user==null) return false;
        user.setUsername(username);
        return true;
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<UserView> findById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::from);
    }

    @Transactional(readOnly = true)
    public List<UserView> findAll() {
        List<UserEntity> users = userRepository.findAll();
        Set<Long> userIds = users.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        Map<Long, List<ProductEntity>> productEntitiesGroupByUserId = productRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(p->p.getUser().getId()));

        return users.stream().map(userEntity -> UserMapper.from(
                userEntity,
                productEntitiesGroupByUserId.getOrDefault(userEntity.getId(), Collections.emptyList())
        )).collect(Collectors.toList());
    }

    @Transactional
    public ProductView addProduct(Long userId, CreateProductDto createProductDto) {
        UserEntity user = userRepository.getReferenceById(userId);
        ProductEntity productEntity = UserMapper.from(user, createProductDto);
        productEntity = productRepository.save(productEntity);
        return UserMapper.from(productEntity);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductView> findByProductByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user->UserMapper.fromProductEntity(user.getProducts()))
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void clear() {
        userRepository.deleteAll();
    }
}
