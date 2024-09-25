package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.Database;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.scenes;

public class Register {
    private Scene registerScene;
    private Database database;

    public Register() {
        database = new Database();

        HBox root = new HBox();
        root.setId("root");
        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER);

        // Add the register scene to the root
        root.getChildren().addAll(getRegister());

        // Set the scene
        registerScene = new Scene(root);
        root.requestFocus();
        registerScene.getStylesheets().add(Application.class.getResource("stylesheets/Register.css").toString());
    }

    // Get the register scene
    public VBox getRegister() {
        // Create VBox for input fields
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setId("container");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setId("username_field");
        usernameField.setPrefWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setId("password_field");
        passwordField.setPrefWidth(200);

        Button registerButton = new Button("Register");
        registerButton.setId("button");
        registerButton.setPrefWidth(200);

        // Check if the input is valid
        registerButton.setOnAction(e -> {
            if (isValidInput(usernameField.getText(), passwordField.getText())) {
                try {
                    registerUser(usernameField.getText(), passwordField.getText());
                } catch (SQLException ex) {
                    showAlert("An error occurred during registration. Please try again.");
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setId("button");
        backButton.setPrefWidth(200);

        // Go back to the login screen
        backButton.setOnAction(e -> {
            showLogin();
        });

        // Add the input fields to the container
        container.getChildren().addAll(usernameField, passwordField, registerButton, backButton);
        return container;
    }

    public boolean isValidInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }
        if (database.isUsernameExists(username)) {
            showAlert("Username is already taken. Please choose another one.");
            return false;
        }
        return true;
    }

    public class PasswordUtils {
        public static String hashPassword(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedHash = digest.digest(password.getBytes());
                return bytesToHex(encodedHash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error hashing password", e);
            }
        }

        private static String bytesToHex(byte[] hash) {
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }


    public void registerUser(String username, String password) throws SQLException {
        if (database.registerUser(username, password)) { // Use the Database method
            User registeredUser = database.authenticateUser(username, password); // Use the Database method

            if (registeredUser != null) {
                Application.setUser(registeredUser);
                showCollection();
            } else {
                showAlert("An error occurred during registration. Please try again.");
            }
        } else {
            showAlert("An error occurred during registration. Please try again.");
        }
    }

    // Show an alert
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    // Get the register scene
    public Scene getRegisterScene() {
        return registerScene;
    }

    // Show the Collection screen
    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    // Show the Login screen
    private void showLogin() {
        Application.mainStage.setScene(scenes.get("Login"));
    }
}