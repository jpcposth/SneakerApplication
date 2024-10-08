package com.example.sneakerapplication;

import java.sql.*;
import java.util.Properties;

public class MySQLConnection {
    private static MySQLConnection instance;
    private Connection connection;
    private final Properties properties;

    public MySQLConnection(String hostname, String port, String database, String username, String password) {
        this.properties = new Properties();
        this.properties.setProperty("hostname", hostname);
        this.properties.setProperty("port", port);
        this.properties.setProperty("database", database);
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", password);
        addConnection();
    }

    public static MySQLConnection getInstance(String hostname, String port, String database, String username, String password) {
        if (instance == null) {
            instance = new MySQLConnection(hostname, port, database, username, password);
        }
        return instance;
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

    public int update(String query, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            return preparedStatement.executeUpdate();
        }
    }

    public ResultSet query(String query, Object... parameters) throws SQLException {
        if (this.connection == null)
            this.addConnection();

        PreparedStatement statement = this.connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }

        return statement.executeQuery();
    }

    public void updateQuery(String query, Object... parameters) throws SQLException {
        if (this.connection == null) {
            this.addConnection();
        }

        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            statement.executeUpdate();
        }
    }
}



