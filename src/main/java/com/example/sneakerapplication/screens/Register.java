package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
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

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.scenes;

public class Register {
    private Scene registerScene;

    public Register() {
        HBox root = new HBox();
        root.setId("root");
        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER);

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
        registerButton.setOnAction(e -> {
            if (isValidInput(usernameField.getText(), passwordField.getText())) {
                registerUser(usernameField.getText(), passwordField.getText());
            }
        });

        Button backButton = new Button("Back");
        backButton.setId("button");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> {
            showLogin();
        });

        container.getChildren().addAll(usernameField, passwordField, registerButton, backButton);
        root.getChildren().addAll(container);

        registerScene = new Scene(root);
        registerScene.getStylesheets().add(Application.class.getResource("stylesheets/register.css").toString());
    }

    private boolean isValidInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }
        if (isUsernameExists(username)) {
            showAlert("Username is already taken. Please choose another one.");
            return false;
        }
        return true;
    }

    private boolean isUsernameExists(String username) {
        try {
            String query =
                    "SELECT * " +
                    "FROM user " +
                    "WHERE username = ?";
            ResultSet resultSet = Application.connection.query(query, username);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void registerUser(String username, String password) {
        try {
            String query =
                    "INSERT INTO user (username, password) " +
                    "VALUES (?, ?)";
            Application.connection.update(query, username, password);

            User registeredUser = authenticateUser(username, password);

            if (registeredUser != null) {
                Application.setUser(registeredUser);

                showCollection();
            } else {
                showAlert("An error occurred during registration. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("An error occurred during registration. Please try again.");
        }
    }

    private User authenticateUser(String username, String password) {
        try {
            String query =
                    "SELECT * " +
                    "FROM user " +
                    "WHERE username = ? AND password = ?";
            ResultSet resultSet = Application.connection.query(query, username, password);

            if (resultSet.next()) {
                return new User(
                        resultSet.getString("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public Scene getRegisterScene() {
        return registerScene;
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    private void showLogin() {
        Application.mainStage.setScene(scenes.get("Login"));
    }
}
