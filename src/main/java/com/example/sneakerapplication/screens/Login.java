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

public class Login {
    private boolean loggedIn = false;
    private Scene loginScene;

    public Login() {
        HBox root = new HBox();
        root.setId("login-root");
        root.setFillHeight(false);
        root.setAlignment(Pos.CENTER);

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setId("login-container");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setId("username-field");
        usernameField.setPrefWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setId("password-field");
        passwordField.setPrefWidth(200);

        Button loginButton = new Button("Log in");
        loginButton.setId("button");
        loginButton.setPrefWidth(200);
        loginButton.setOnAction(e -> {
            if (isValidCredentials(usernameField.getText(), passwordField.getText())) {
                showCollection();
            }
        });

        container.getChildren().addAll(usernameField, passwordField, loginButton);
        root.getChildren().addAll(container);

        loginScene = new Scene(root);
        loginScene.getStylesheets().add(Application.class.getResource("stylesheets/login.css").toString());
    }


    private boolean isValidCredentials(String username, String password) {
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

        ResultSet resultSet = Application.connection.query(query, username, password);
        if (resultSet.next()) {
            return new User(
                    resultSet.getString("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return loginScene;
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }
}
