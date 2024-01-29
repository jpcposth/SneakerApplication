package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private final String user_id, username, password;

    public User(String user_id, String username, String password) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
    }
    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}