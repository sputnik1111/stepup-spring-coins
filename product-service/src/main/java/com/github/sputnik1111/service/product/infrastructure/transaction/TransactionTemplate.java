package com.github.sputnik1111.service.product.infrastructure.transaction;


import com.github.sputnik1111.service.product.infrastructure.jdbc.ConnectionManager;
import com.github.sputnik1111.service.product.infrastructure.jdbc.ConnectionWrapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

@Component
public class TransactionTemplate implements ConnectionManager {
    private final ThreadLocal<TransactionConnection> transactionConnection = new ThreadLocal<>();

    private final DataSource dataSource;

    public TransactionTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T inTransaction(Supplier<T> callback) {
        return transactionConnection.get() == null
                ? startNewTransaction(callback)
                : callback.get();//propagate exist transaction

    }

    private <T> T startNewTransaction(Supplier<T> callback) {
        try (Connection connection = dataSource.getConnection()) {
            boolean restoreAutoCommit = connection.getAutoCommit();
            try {
                transactionConnection.set(new TransactionConnection(connection));
                if (restoreAutoCommit) {
                    connection.setAutoCommit(false);
                }
                T result = callback.get();
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                if (restoreAutoCommit) connection.setAutoCommit(true);
                transactionConnection.remove();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Connection getConnection() throws SQLException {
        return transactionConnection.get() != null
                ? transactionConnection.get()
                : dataSource.getConnection();
    }

    private static class TransactionConnection extends ConnectionWrapper {

        public TransactionConnection(Connection delegate) {
            super(delegate);
        }

        @Override
        public void close() {
            //ignore
        }
    }
}