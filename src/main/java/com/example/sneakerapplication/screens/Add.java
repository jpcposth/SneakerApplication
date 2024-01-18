package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.*;

import static com.example.sneakerapplication.Application.applicationSize;
import static com.example.sneakerapplication.Application.scenes;

public class Add {
    private Scene addScene;
    public Add() {
        Pane container = new Pane();
        container.setId("container");

        VBox inputFields = new VBox(20);
        inputFields.setAlignment(Pos.CENTER);
        inputFields.setPadding(new Insets(50));
        inputFields.relocate(applicationSize[0] / 2 - 550, applicationSize[1] / 2 - 475);
        inputFields.setId("inputfields");

        TextField image = new TextField();
        image.setPromptText("Image URL");
        image.setId("input");
        image.setPrefWidth(400);

        TextField brand = new TextField();
        brand.setPromptText("Brand");
        brand.setId("input");
        brand.setPrefWidth(400);

        TextField model = new TextField();
        model.setPromptText("Model");
        model.setId("input");
        model.setPrefWidth(400);

        TextField size = new TextField();
        size.setPromptText("Size");
        size.setId("input");
        size.setPrefWidth(400);

        TextField release_date = new TextField();
        release_date.setPromptText("Release date YYYY/MM/DD");
        release_date.setId("input");
        release_date.setPrefWidth(400);

        TextField purchase_date = new TextField();
        purchase_date.setPromptText("Purchase date YYYY/MM/DD");
        purchase_date.setId("input");
        purchase_date.setPrefWidth(400);

        TextField price = new TextField();
        price.setPromptText("Price");
        price.setId("input");
        price.setPrefWidth(400);

        Button addButton = new Button("Add");
        addButton.setId("add-button");
        addButton.setPrefWidth(400);
//        addButton.setOnAction(e -> {
//            if (isValidCredentials(image.getText(), brand.getText(), model.getText(), size.getText(), release_date.getText(), purchase_date.getText(), price.getText() {
//                System.out.println(gelukt);
//            }
//        });
        addButton.setOnAction(e -> {
            if (!image.getText().isEmpty() && !brand.getText().isEmpty() && !model.getText().isEmpty()
                    && !size.getText().isEmpty() && !release_date.getText().isEmpty()
                    && !purchase_date.getText().isEmpty() && !price.getText().isEmpty()) {
                System.out.println("Correct");
            }
        });


        inputFields.getChildren().addAll(image, brand, model, size, release_date, purchase_date, price, addButton);
        container.getChildren().addAll(getNavBar(),inputFields);


        addScene = new Scene(container);
        addScene.getStylesheets().add(Application.class.getResource("stylesheets/add.css").toString());
    }

    private void addSneakers() {

    }

//    private boolean isValidCredentials(String username, String password) {
//        if (username.isEmpty() || password.isEmpty()) {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Error");
//            alert.setHeaderText("Please fill in all fields.");
//            alert.showAndWait();
//            return false;
//        }
//
//        String query = "SELECT * FROM User WHERE username = '" + username + "' AND password = '" + password + "'";
//
//        try {
//            ResultSet resultSet = Application.connection.query(query);
//            if (resultSet.next()) {
//                User loggedInUser = new User(
//                        resultSet.getString("user_id"),
//                        resultSet.getString("username"),
//                        resultSet.getString("password")
//                );
//
//                Application.setUser(loggedInUser);
//                loggedIn = true;
//                return true;
//            } else {
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("Error");
//                alert.setHeaderText("Invalid username or password. Please try again.");
//                alert.showAndWait();
//            }
//        } catch (SQLException e) {
//            System.out.println("An error occurred during login. Please try again.");
//        }
//        return false;
//    }

    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, applicationSize[1]);
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
