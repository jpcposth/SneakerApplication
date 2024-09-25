package com.example.sneakerapplication;

import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.User;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

class DatabaseTest {
    @Test
    void registerUser() {
        Database db = new Database();
        db.registerUser("Test", "Test");
    }

    @Test
    void authenticateUser() throws SQLException {
        Database db = new Database();
        db.authenticateUser("Test", "532eaabd9574880dbf76b9b8cc00832c20a6ec113d682299550d7a6e0f345e25");
    }

    @Test
    void isUsernameExists() {
        Database db = new Database();
        db.isUsernameExists("Test");
    }

    @Test
    void getSneakersForUser() throws SQLException {
        Database db = new Database();
        db.getSneakersForUser(new User(2, "username", "hashed_password"), null);
    }

    @Test
    void getDistinctBrandsForUser() throws SQLException {
        Database db = new Database();
        db.getDistinctBrandsForUser(new User(2, "username", "hashed_password"));
    }

    @Test
    void addSneaker() throws SQLException {
        Database db = new Database();
        db.addSneaker("test_image.png", "test_brand", "test_model", "42", "2000-01-01", "2000-01-01", "100", new User(2, "username", "hashed_password"));
    }

    @Test
    void addBrand() throws SQLException {
        Database db = new Database();
        db.addBrand("test");
    }

    @Test
    void addModel() throws SQLException {
        Database db = new Database();
        db.addModel("test_model", new Brand(1, "test_brand"));
    }

    @Test
    void getSneakerById() {
        Database db = new Database();
        db.getSneakerById(1);
    }

    @Test
    void updateSneaker() {
        Database db = new Database();
        db.updateSneaker(100, "updated_image.png", "updated_brand", "updated_model", "43", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), "100");
    }

    @Test
    void deleteSneaker() {
        Database db = new Database();
        db.deleteSneaker(100);
    }

    @Test
    void getBrandWithMostSneakers() throws SQLException {
        Database db = new Database();
        db.getBrandWithMostSneakers(new User(1, "username", "hashed_password"));
    }

    @Test
    void getTotalPrice() throws SQLException {
        Database db = new Database();
        db.getTotalPrice(new User(1, "username", "hashed_password"));
    }

    @Test
    void getTotalSneakers() throws SQLException {
        Database db = new Database();
        db.getTotalSneakers(new User(1, "username", "hashed_password"));
    }
}