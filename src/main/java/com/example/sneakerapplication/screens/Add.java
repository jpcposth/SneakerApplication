package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.Database;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.*;

import static com.example.sneakerapplication.Application.*;

public class Add {
    private Scene addScene;
    private Database database;

    public Add() {
        database = new Database();

        Pane container = new Pane();
        container.setId("container");

        // Add navbar and input fields to the container
        container.getChildren().addAll(getNavBar(),getInput());

        // Set the scene
        addScene = new Scene(container);
        container.requestFocus();
        addScene.getStylesheets().add(Application.class.getResource("stylesheets/Add.css").toString());
    }

    // Get the inputs
    public Pane getInput() {
        // Create VBox for input fields
        VBox inputFields = new VBox(20);
        inputFields.setPadding(new Insets(50));;
        inputFields.relocate((applicationSize[0]-getNavBar().getPrefWidth())/2, 210);
        inputFields.setId("inputfields");

        // Create input fields
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

        DatePicker releaseDate = new DatePicker();
        releaseDate.setPromptText("Release date");
        releaseDate.setId("input");
        releaseDate.setPrefWidth(400);
        releaseDate.setEditable(false);

        DatePicker purchaseDate = new DatePicker();
        purchaseDate.setPromptText("Purchase date");
        purchaseDate.setId("input");
        purchaseDate.setPrefWidth(400);
        purchaseDate.setEditable(false);

        TextField price = new TextField();
        price.setPromptText("Price");
        price.setId("input");
        price.setPrefWidth(400);

        Button addButton = new Button("Add");
        addButton.setId("add-button");
        addButton.setPrefWidth(400);

        // Check if all fields are filled in
        addButton.setOnAction(e -> {
            if (!image.getText().isEmpty() && !brand.getText().isEmpty() && !model.getText().isEmpty()
                    && !size.getText().isEmpty() && releaseDate.getValue() != null
                    && purchaseDate.getValue() != null && !price.getText().isEmpty()) {
                try {
                    database.addSneaker(image.getText(), brand.getText(), model.getText(),
                            size.getText(), releaseDate.getValue().toString(), purchaseDate.getValue().toString(),
                            price.getText(), Application.getLoggedInUser()); // Pass logged-in user here
                    showCollection();
                } catch (SQLException ex) {
                    showAlert("Error while adding sneaker: " + ex.getMessage());
                }
            } else {
                showAlert("Please fill in all fields.");
            }
        });


        // Add input fields to the VBox
        inputFields.getChildren().addAll(image, brand, model, size, releaseDate, purchaseDate, price, addButton);
        return inputFields;
    }

    // Get navbar
    public Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, applicationSize[1]);
        navBar.setPadding(new Insets(40, 0, 0, 0));
        navBar.getChildren().addAll(
                generateNavItem("Collection", false, this::showCollection),
                generateNavItem("Add", true, () -> {}),
                generateNavItem("Statistics", false, this::showStatistics));
        return navBar;
    }

    // Generate nav item
    public FlowPane generateNavItem(String title, boolean active, Runnable onClick) {
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

    // Show an alert
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    // Get the add scene
    public Scene getAddScene() {
        return addScene;
    }

    // Show the Collection screen
    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    // Show the Statistics screen
    private void showStatistics() {
        scenes.put("Statistics", new Statistics().getStatisticsScene());
        Application.mainStage.setScene(scenes.get("Statistics"));
    }
}
