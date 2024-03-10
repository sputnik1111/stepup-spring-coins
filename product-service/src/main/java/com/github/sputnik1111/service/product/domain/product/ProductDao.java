package com.github.sputnik1111.service.product.domain.product;

import com.github.sputnik1111.service.product.infrastructure.jdbc.JdbcTemplate;
import com.github.sputnik1111.service.product.infrastructure.jdbc.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductDao {

    private final JdbcTemplate template;

    private final RowMapper<ProductEntity> productMapper = (rs) -> new ProductEntity(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("account"),
            rs.getLong("balance"),
            Enum.valueOf(TypeProduct.class, rs.getString("type_product"))
    );

    private final RowMapper<Long> productIdMapper = (rs) -> rs.getLong("id");

    private static final String SELECT_ALL_PRODUCT =
            "SELECT id,user_id,account,balance,type_product FROM user_product";

    public ProductDao(JdbcTemplate template) {
        this.template = template;
    }

    public Long insert(ProductEntity productEntity) {
        String sql = "INSERT INTO user_product (user_id,account,balance,type_product) values (?,?,?,?) RETURNING id";
        return template.executeQuery(
                sql,
                preparedStatement -> {
                    preparedStatement.setLong(1, productEntity.getUserId());
                    preparedStatement.setString(2, productEntity.getAccount());
                    preparedStatement.setLong(3, productEntity.getBalance());
                    preparedStatement.setString(4, productEntity.getTypeProduct().toString());
                },
                productIdMapper
        ).get(0);
    }

    public Optional<ProductEntity> findById(Long productId) {
        String sql = SELECT_ALL_PRODUCT + " WHERE id = ?";
        List<ProductEntity> users = template.executeQuery(
                sql,
                preparedStatement -> preparedStatement.setLong(1, productId),
                productMapper
        );
        return users.isEmpty()
                ? Optional.empty()
                : Optional.of(users.get(0));
    }

    public List<ProductEntity> findByUserId(Long userId) {
        String sql = SELECT_ALL_PRODUCT + " WHERE user_id = ?";
        return template.executeQuery(
                sql,
                preparedStatement -> preparedStatement.setLong(1, userId),
                productMapper
        );
    }

    public List<ProductEntity> findByUserIds(Collection<Long> userIds) {
        if (userIds.isEmpty()) return Collections.emptyList();
        String idsStr = userIds.stream().map(e -> "?")
                .collect(Collectors.joining(","));
        String sql = SELECT_ALL_PRODUCT + String.format(" WHERE user_id = (%s)", idsStr);
        return template.executeQuery(
                sql,
                preparedStatement -> {
                    int i = 1;
                    for (Long id : userIds) preparedStatement.setLong(i++, id);
                },
                productMapper
        );
    }

    public boolean delete(Long productId) {
        String sql = "DELETE FROM user_product WHERE id = ? ";
        return template.execute(sql, preparedStatement -> {
            preparedStatement.setLong(1, productId);
            return preparedStatement.executeUpdate() == 1;
        });
    }


}
