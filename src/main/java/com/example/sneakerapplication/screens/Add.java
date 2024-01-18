package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import static com.example.sneakerapplication.Application.scenes;

public class Add {
    private Scene addScene;
    public Add() {
        Pane container = new Pane();
        container.setId("container");

        container.getChildren().addAll(getNavBar());


        addScene = new Scene(container);
        addScene.getStylesheets().add(Application.class.getResource("stylesheets/add.css").toString());
    }
    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, Application.applicationSize[1]);
        navBar.setPadding(new Insets(80, 0, 0, 0));
        navBar.getChildren().addAll(
                generateNavItem("Collection", false, this::showCollection),
                generateNavItem("Add", true, null),
                generateNavItem("Statistics", false, null));
        return navBar;
    }

    private FlowPane generateNavItem(String title, boolean active, Runnable onClick) {
        FlowPane navItem = new FlowPane();
        navItem.setId("nav_item");
        navItem.setPadding(new Insets(0, 0, 0, 20));
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setPrefSize(250, 35);
        navItem.setHgap(40);

        Text navItemText = new Text(title);
        navItemText.setId("nav_item_text");
        navItem.getChildren().addAll(navItemText);

        if (active) {
            navItem.getStyleClass().add("active");
        } else {
            navItem.getStyleClass().add("inactive");
        }

        navItem.setOnMouseClicked(event -> onClick.run());

        return navItem;
    }
    public Scene getAddScene() {
        return addScene;
    }

    private void showCollection() {
        Application.mainStage.setScene(scenes.get("Collection"));
    }

//    private void showStatistics() {
//        scenes.put("Statistics", new Statistics().getStatisticsScene());
//        Application.mainStage.setScene(scenes.get("Statistics"));
//    }
}
