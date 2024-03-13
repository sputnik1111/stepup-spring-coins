package com.github.sputnik1111.service.product.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    List<ProductEntity> findByUserId(Long userId);

    List<ProductEntity> findByUserIdIn(Collection<Long> userIds);
}
