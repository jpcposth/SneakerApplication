package com.example.sneakerapplication;

import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.User;
import com.example.sneakerapplication.screens.Register;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Database {
    public MySQLConnection connection;

    public Database () {
        connection = MySQLConnection.getInstance("adainforma.tk", "3306", "bp2_sneakerapp", "sneakerapp", "f0oh4A9~9");
        if (connection == null) {
            System.out.println("Failed to connect to the database. Please check your connection settings.");
        }
    }

    public User authenticateUser(String username, String password) throws SQLException {
        // Hash the password before comparing it
        String hashedPassword = Register.PasswordUtils.hashPassword(password);

        String query =
                "SELECT * " +
                "FROM user " +
                "WHERE username = ? AND password = ?";

        ResultSet resultSet = connection.query(query, username, hashedPassword);
        if (resultSet.next()) {
            return new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }


    public boolean registerUser(String username, String password) {
        try {
            // Hash the password before storing it
            String hashedPassword = Register.PasswordUtils.hashPassword(password);

            String query =
                    "INSERT INTO user (username, password) " +
                    "VALUES (?, ?)";
            connection.update(query, username, hashedPassword);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Checks if the username already exists in the database
    public boolean isUsernameExists(String username) {
        try {
            String query =
                    "SELECT * " +
                    "FROM user " +
                    "WHERE username = ?";
            ResultSet resultSet = connection.query(query, username);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all sneakers for a specific user
    public ResultSet getSneakersForUser(User loggedInUser, String brandFilter) throws SQLException {
        String query =
                "SELECT * " +
                "FROM sneaker s " +
                "JOIN model m ON s.model_id = m.model_id " +
                "JOIN brand b ON m.brand_id = b.brand_id " +
                "WHERE s.user_id = ?";

        if (brandFilter != null && !brandFilter.equals("All")) {
            query += " AND b.brand = ?";
        }

        // Add ordering by purchase_date DESC
        query += " ORDER BY s.purchase_date DESC";

        if (brandFilter != null && !brandFilter.equals("All")) {
            return connection.query(query, loggedInUser.getUser_id(), brandFilter);
        } else {
            return connection.query(query, loggedInUser.getUser_id());
        }
    }


    // Get all distinct brands for a specific user
    public ResultSet getDistinctBrandsForUser(User loggedInUser) throws SQLException {
        String query =
                "SELECT DISTINCT b.brand " +
                "FROM sneaker s " +
                "JOIN model m ON s.model_id = m.model_id " +
                "JOIN brand b ON m.brand_id = b.brand_id " +
                "WHERE s.user_id = ? " +
                "ORDER BY b.brand DESC;";
        return connection.query(query, loggedInUser.getUser_id());
    }

    // Add a new sneaker to the database
    public void addSneaker(String image, String brandName, String modelName, String size, String releaseDate, String purchaseDate, String price, User loggedInUser) throws SQLException {
        if (loggedInUser != null) {
            Brand brand = addBrand(brandName);
            Model model = addModel(modelName, brand);

            String insertQuery =
                    "INSERT INTO sneaker (image, user_id, model_id, size, release_date, purchase_date, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            connection.updateQuery(insertQuery, image, loggedInUser.getUser_id(), model.getModel_id(), size, releaseDate, purchaseDate, price);
        }
    }

    // Add a new brand to the database
    public Brand addBrand(String brandName) throws SQLException {
        String brandQuery =
                "SELECT * " +
                "FROM brand " +
                "WHERE brand = ?";
        ResultSet brandResult = connection.query(brandQuery, brandName);

        if (brandResult.next()) {
            return new Brand(brandResult);
        } else {
            String insertBrandQuery =
                    "INSERT INTO brand (brand) VALUES (?)";
            connection.updateQuery(insertBrandQuery, brandName);

            ResultSet insertedBrandResult = connection.query(brandQuery, brandName);
            insertedBrandResult.next();
            return new Brand(insertedBrandResult);
        }
    }

    // Add a new model to the database
    public Model addModel(String modelName, Brand brand) throws SQLException {
        String modelQuery =
                "SELECT * " +
                "FROM model " +
                "WHERE model = ? AND brand_id = ?";
        ResultSet modelResult = connection.query(modelQuery, modelName, brand.getBrand_id());

        if (modelResult.next()) {
            return new Model(modelResult);
        } else {
            String insertModelQuery =
                    "INSERT INTO model (model, brand_id) " +
                    "VALUES (?, ?)";
            connection.updateQuery(insertModelQuery, modelName, brand.getBrand_id());

            ResultSet insertedModelResult = connection.query(modelQuery, modelName, brand.getBrand_id());
            insertedModelResult.next();
            return new Model(insertedModelResult);
        }
    }

    public ResultSet getSneakerById(int sneakerId) {
        try {
            String query =
                    "SELECT * " +
                    "FROM sneaker s " +
                    "JOIN model m ON s.model_id = m.model_id " +
                    "JOIN brand b ON m.brand_id = b.brand_id " +
                    "WHERE s.sneaker_id = ?";
            return connection.query(query, sneakerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSneaker(int sneakerId) {
        try {
            String deleteQuery =
                    "DELETE FROM sneaker " +
                    "WHERE sneaker_id = ?";
            connection.updateQuery(deleteQuery, sneakerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSneaker(int sneakerId, String image, String brandName, String modelName, String size, LocalDate releaseDate, LocalDate purchaseDate, String price) {
        try {
            // Update or insert the brand and model
            Brand brand = updateBrand(brandName);
            Model model = updateModel(modelName, brand);

            String updateQuery =
                    "UPDATE sneaker " +
                    "SET image = ?, model_id = ?, size = ?, release_date = ?, purchase_date = ?, price = ? " +
                    "WHERE sneaker_id = ?";
            connection.updateQuery(updateQuery, image, model.getModel_id(), size, releaseDate, purchaseDate, price, sneakerId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Brand updateBrand(String brandName) {
        try {
            // Check if the brand already exists
            String checkBrandQuery =
                    "SELECT * " +
                    "FROM brand " +
                    "WHERE brand = ?";
            ResultSet resultSet = connection.query(checkBrandQuery, brandName);

            // If brand exists, return the brand object
            if (resultSet.next()) {
                int brandId = resultSet.getInt("brand_id"); // Correctly retrieve as int
                return new Brand(brandId, brandName);
            }

            // If brand does not exist, insert the new brand
            String insertBrandQuery =
                    "INSERT INTO brand (brand) " +
                    "VALUES (?)";
            connection.updateQuery(insertBrandQuery, brandName);

            // Retrieve the inserted brand's ID
            resultSet = connection.query(checkBrandQuery, brandName);
            if (resultSet.next()) {
                int brandId = resultSet.getInt("brand_id");
                return new Brand(brandId, brandName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Model updateModel(String modelName, Brand brand) {
        try {
            // Check if the model already exists for this brand
            String checkModelQuery =
                    "SELECT * " +
                    "FROM model " +
                    "WHERE model = ? AND brand_id = ?";
            ResultSet resultSet = connection.query(checkModelQuery, modelName, brand.getBrand_id()); // <-- This line

            // If model exists, return the model object
            if (resultSet.next()) {
                int modelId = resultSet.getInt("model_id");
                return new Model(modelId, modelName, brand.getBrand_id());
            }

            // If model does not exist, insert the new model
            String insertModelQuery =
                    "INSERT INTO model (model, brand_id) " +
                    "VALUES (?, ?)";
            connection.updateQuery(insertModelQuery, modelName, brand.getBrand_id());

            // Retrieve the inserted model's ID
            resultSet = connection.query(checkModelQuery, modelName, brand.getBrand_id());
            if (resultSet.next()) {
                int modelId = resultSet.getInt("model_id");
                return new Model(modelId, modelName, brand.getBrand_id());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getBrandWithMostSneakers(User user) throws SQLException {
        if (user != null) {
            String query =
                    "SELECT brand.brand, COUNT(sneaker.sneaker_id) AS total_sneakers " +
                    "FROM brand " +
                    "JOIN model ON brand.brand_id = model.brand_id " +
                    "JOIN sneaker ON model.model_id = sneaker.model_id " +
                    "WHERE sneaker.user_id = ? " +
                    "GROUP BY brand.brand " +
                    "ORDER BY total_sneakers DESC " +
                    "LIMIT 1";

            ResultSet resultSet = connection.query(query, user.getUser_id());

            if (resultSet.next()) {
                String brandName = resultSet.getString("brand");
                int totalSneakers = resultSet.getInt("total_sneakers");
                return new String[]{brandName, String.valueOf(totalSneakers)};
            }
        }
        return new String[]{"", "0"};
    }

    // Get total price of sneakers for a user
    public double getTotalPrice(User user) throws SQLException {
        if (user != null) {
            String query =
                    "SELECT SUM(price) AS total_price " +
                    "FROM sneaker " +
                    "WHERE user_id = ?";
            ResultSet resultSet = connection.query(query, user.getUser_id());

            if (resultSet.next()) {
                return resultSet.getDouble("total_price");
            }
        }
        return 0.0;
    }

    // Get total number of sneakers for a user
    public int getTotalSneakers(User user) throws SQLException {
        if (user != null) {
            String query =
                    "SELECT COUNT(sneaker_id) AS total_sneakers " +
                    "FROM sneaker " +
                    "WHERE user_id = ?";
            ResultSet resultSet = connection.query(query, user.getUser_id());

            if (resultSet.next()) {
                return resultSet.getInt("total_sneakers");
            }
        }
        return 0;
    }
}
