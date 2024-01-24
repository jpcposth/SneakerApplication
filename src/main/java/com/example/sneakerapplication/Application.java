package com.example.sneakerapplication;

import com.example.sneakerapplication.classes.User;
import com.example.sneakerapplication.screens.Add;
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
    public static int[] applicationSize = {1536, 960};
    @Override
    public void start(Stage stage) throws IOException {
        connection = new MySQLConnection("adainforma.tk", "3306", "bp2_sneakerapp", "sneakerapp", "f0oh4A9~9");

        scenes.put("Login", new Login().getScene());
        scenes.put("Add", new Add().getAddScene());

        mainStage = stage;

        mainStage.setWidth(applicationSize[0]);
        mainStage.setHeight(applicationSize[1]);
        mainStage.setResizable(false);
        mainStage.setTitle("Sneakers");

        mainStage.setScene(scenes.get("Login"));
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}