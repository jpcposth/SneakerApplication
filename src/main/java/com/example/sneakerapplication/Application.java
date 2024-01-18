package com.example.sneakerapplication;

import com.example.sneakerapplication.classes.User;
import com.example.sneakerapplication.screens.Login;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;


public class Application extends javafx.application.Application {
    public static User getLoggedInUser() {
        return loggedInUser;
    }
    private static User loggedInUser;
    public static void setUser(User user) {
        loggedInUser = user;
    }
    public static Stage mainStage;
    public static HashMap<String, Scene> scenes = new HashMap<>();
    public static MySQLConnection connection;
    public static int[] applicationSize = {2560, 1440};
    @Override
    public void start(Stage stage) throws IOException {
        connection = new MySQLConnection("127.0.0.1", "3306", "sneaker_app", "root", "");

        scenes.put("Login", new Login().getScene());

        mainStage = stage;

        mainStage.setWidth(applicationSize[0]);
        mainStage.setHeight(applicationSize[1]);
        mainStage.setResizable(true);
        mainStage.setMaximized(true);
        mainStage.setTitle("Sneakers");

        mainStage.setScene(scenes.get("Login"));
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}