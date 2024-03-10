package com.github.sputnik1111.service.product.domain.user;

import com.github.sputnik1111.service.product.infrastructure.jdbc.JdbcTemplate;
import com.github.sputnik1111.service.product.infrastructure.jdbc.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserDao {

    private final JdbcTemplate template;

    private final RowMapper<UserEntity> userMapper = (rs) -> new UserEntity(
            rs.getLong("id"),
            rs.getString("username")
    );

    private final RowMapper<Long> userIdMapper = (rs) -> rs.getLong("id");


    public UserDao(JdbcTemplate template) {
        this.template = template;
    }

    public Long insert(String username) {
        String sql = "INSERT INTO users (username) values (?) RETURNING id";
        return template.executeQuery(
                sql,
                preparedStatement -> preparedStatement.setString(1, username),
                userIdMapper
        ).get(0);
    }

    public boolean update(Long userId, String userName) {
        String sql = "UPDATE users SET username = ? WHERE id = ? ";
        return template.execute(sql, preparedStatement -> {
            preparedStatement.setString(1, userName);
            preparedStatement.setLong(2, userId);
            return preparedStatement.executeUpdate() == 1;
        });
    }

    public boolean delete(Long userId) {
        String sql = "DELETE FROM users WHERE id = ? ";
        return template.execute(sql, preparedStatement -> {
            preparedStatement.setLong(1, userId);
            return preparedStatement.executeUpdate() == 1;
        });
    }

    public Optional<UserEntity> findById(Long userId) {
        String sql = "SELECT id,username FROM users WHERE id = ?";
        List<UserEntity> userEntities = template.executeQuery(
                sql,
                preparedStatement -> preparedStatement.setLong(1, userId),
                userMapper
        );
        return userEntities.isEmpty()
                ? Optional.empty()
                : Optional.of(userEntities.get(0));
    }

    public List<UserEntity> findAll() {
        String sql = "SELECT id,username FROM users";
        return template.executeQuery(
                sql,
                preparedStatement -> {
                },
                userMapper
        );
    }

    public void clear() {
        String sql = "TRUNCATE TABLE users CASCADE";
        template.execute(sql, preparedStatement ->
                preparedStatement.executeUpdate() == 1
        );
    }



}
