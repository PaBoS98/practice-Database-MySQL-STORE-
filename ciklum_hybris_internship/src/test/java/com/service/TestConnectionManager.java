package com.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnectionManager {

    private String url = "jdbc:mysql://localhost:3306/test_orderdb?useSSL=false&serverTimezone=UTC";
    private String username = "root";
    private String password = "root";

    @Test
    public void testCreateConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        Connection testConnection = ConnectionManager.getConnection(url, username, password);
        Assertions.assertEquals(connection.getSchema(), testConnection.getSchema());
    }
}
