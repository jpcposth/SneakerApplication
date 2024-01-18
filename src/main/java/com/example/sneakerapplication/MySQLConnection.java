package com.example.sneakerapplication;

import java.sql.*;
import java.util.Properties;

public class MySQLConnection {
    private Connection connection;
    private final Properties properties;

    public MySQLConnection(String hostname, String port, String database, String username, String password) {
        this.properties = new Properties();
        this.properties.setProperty("hostname", hostname);
        this.properties.setProperty("port", port);
        this.properties.setProperty("database", database);
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", password);
    }

    public void addConnection() {
        String url = "jdbc:mysql://%s:%s/%s".formatted(
                this.properties.getProperty("hostname"),
                this.properties.getProperty("port"),
                this.properties.getProperty("database")
        );

        try {
            this.connection = DriverManager.getConnection(url, this.properties);
        } catch(SQLException ex) {
            this.connection = null;

            System.out.println("An error occurred while connecting MySQL database");
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet query(String query) throws SQLException {
        if (this.connection == null)
            this.addConnection();

        Statement statement = this.connection.createStatement();
        return statement.executeQuery(query);
    }

    public ResultSet query(String query, String userId) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query);
        statement.setString(1, userId);
        return statement.executeQuery();
    }
}


