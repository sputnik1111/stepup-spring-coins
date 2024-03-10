package com.github.sputnik1111.service.product.infrastructure.jdbc;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTemplate {
    private final ConnectionManager connectionManager;

    public JdbcTemplate(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public <T> T execute(
            String sql,
            StatementCallBack<T> callBack
    ) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            return callBack.execute(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> executeQuery(String sql, StatementSetter statementSetter, RowMapper<T> rowMapper) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            statementSetter.execute(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSet(resultSet, rowMapper);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> mapResultSet(ResultSet rs, RowMapper<T> rowMapper) throws SQLException {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowMapper.map(rs));
        }
        return result;
    }


    @FunctionalInterface
    public interface StatementCallBack<T> {
        T execute(PreparedStatement preparedStatement) throws SQLException;
    }

    @FunctionalInterface
    public interface StatementSetter {
        void execute(PreparedStatement preparedStatement) throws SQLException;
    }
}
