package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private final String username, password;
    private final int user_id;

    public User(int user_id, String username, String password) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
    }
    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}