package com.example.sneakerapplication;

import com.example.sneakerapplication.classes.Sneaker;
import com.example.sneakerapplication.screens.Collection;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;


public class Application extends javafx.application.Application {
    public static Stage mainStage;
    public static HashMap<String, Scene> scenes = new HashMap<>();
    public static MySQLConnector connection;
//    public static MySQLConnection connection;
    public static int[] applicationSize = {1280, 720};
    @Override
    public void start(Stage stage) throws IOException {
        connection = new MySQLConnector("127.0.0.1", "3306", "sneaker_app", "root", "");

        scenes.put("Collection", new Collection().getScene());

        mainStage = stage;

        mainStage.setWidth(applicationSize[0]);
        mainStage.setHeight(applicationSize[1]);
        mainStage.setResizable(true );
//        mainStage.setFullScreen(true);
        mainStage.setTitle("Collection");

        mainStage.setScene(scenes.get("Collection"));
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}