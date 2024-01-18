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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.scenes;

public class Login {
    private boolean loggedIn = false;
    private Scene loginScene;

    public Login() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));

        Text title = new Text("Login");
        title.setStyle("-fx-font-size: 24px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            if (isValidCredentials(usernameField.getText(), passwordField.getText())) {
                showCollection();
            }
        });


        container.getChildren().addAll(title, usernameField, passwordField, loginButton);

        loginScene = new Scene(container, 300, 200);
        loginScene.getStylesheets().add(Application.class.getResource("stylesheets/login.css").toString());
    }

    private boolean isValidCredentials(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return false;
        }

        String query = "SELECT * FROM User WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            ResultSet resultSet = Application.connection.query(query);
            if (resultSet.next()) {
                User loggedInUser = new User(
                        resultSet.getString("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );

                Application.setUser(loggedInUser);
                loggedIn = true;
                return true;
            } else {
                showError("Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            showError("An error occurred during login. Please try again.");
        }
        return false;
    }

    private void showError(String message) {
        System.out.println("Error: " + message);
    }

    public Scene getScene() {
        return loginScene;
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }
}
