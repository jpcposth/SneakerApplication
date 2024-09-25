package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.Database;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

import static com.example.sneakerapplication.Application.scenes;

public class Login {
    private boolean loggedIn = false;
    private Scene loginScene;
    private Database database;

    public Login() {
        database = new Database();

        HBox root = new HBox();
        root.setId("root");
        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER);

        // Add the login scene to the root
        root.getChildren().addAll(getLogin());

        // Set the scene
        loginScene = new Scene(root);
        root.requestFocus();
        loginScene.getStylesheets().add(Application.class.getResource("stylesheets/Login.css").toString());
    }

    // Get the login scene
    public VBox getLogin() {
        // Create VBox for input fields
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setId("container");

        // Create input fields
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

        // Checks if input is valid
        loginButton.setOnAction(e -> {
            if (isValidInput(usernameField.getText(), passwordField.getText())) {
                showCollection();
            }
        });

        Label registerLabel = new Label("Don't have an account?");

        Button registerButton = new Button("Register");
        registerButton.setId("button");
        registerButton.setPrefWidth(200);

        // Goes to register screen
        registerButton.setOnAction(e -> {
            showRegister();
        });

        // Add input fields to the container
        container.getChildren().addAll(usernameField, passwordField, loginButton, registerLabel, registerButton);
        return container;
    }

    /**
     * Checks if the input for user login is valid
     * @param username the username input
     * @param password the password input
     */
    private boolean isValidInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields.");
            return false;
        }

        try {
            User loggedInUser = database.authenticateUser(username, password);
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

    // Show an alert
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    // Get the login scene
    public Scene getLoginScene() {
        return loginScene;
    }

    // Show the Register screen
    private void showRegister() {
        Application.mainStage.setScene(scenes.get("Register"));
    }

    // Show the Collection screen
    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }
}