package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.connection;
import static com.example.sneakerapplication.Application.scenes;

public class Login {
    private boolean loggedIn = false;
    private Scene loginScene;

    public Login() {
        HBox root = new HBox();
        root.setId("root");
        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(getLogin());

        loginScene = new Scene(root);
        root.requestFocus();
        loginScene.getStylesheets().add(Application.class.getResource("stylesheets/Login.css").toString());
    }

    public VBox getLogin() {
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

        Button loginButton = new Button("Log in");
        loginButton.setId("button");
        loginButton.setPrefWidth(200);
        loginButton.setOnAction(e -> {
            if (isValidInput(usernameField.getText(), passwordField.getText())) {
                showCollection();
            }
        });

        Label registerLabel = new Label("Don't have an account?");

        Button registerButton = new Button("Register");
        registerButton.setId("button");
        registerButton.setPrefWidth(200);
        registerButton.setOnAction(e -> {
            showRegister();
        });

        container.getChildren().addAll(usernameField, passwordField, loginButton, registerLabel, registerButton);
        return container;
    }


    private boolean isValidInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }

        try {
            User loggedInUser = authenticateUser(username, password);
            if (loggedInUser != null) {
                Application.setUser(loggedInUser);
                loggedIn = true;
                return true;
            } else {
                showAlert("Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred during login. Please try again.");
        }
        return false;
    }

    private User authenticateUser(String username, String password) throws SQLException {
        String query =
                "SELECT * " +
                "FROM user " +
                "WHERE username = ? AND password = ?";

        ResultSet resultSet = connection.query(query, username, password);
        if (resultSet.next()) {
            return new User(
                    resultSet.getString("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public Scene getLoginScene() {
        return loginScene;
    }

    private void showRegister() {
        Application.mainStage.setScene(scenes.get("Register"));
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }
}
