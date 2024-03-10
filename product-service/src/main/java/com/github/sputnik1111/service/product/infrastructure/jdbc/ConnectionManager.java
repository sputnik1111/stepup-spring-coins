package com.github.sputnik1111.service.product.infrastructure.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {
    Connection getConnection() throws SQLException;
}
