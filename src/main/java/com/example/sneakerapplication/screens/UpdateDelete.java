package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.Database;
import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.example.sneakerapplication.Application.*;

public class UpdateDelete {
    private Scene updateDeleteScene;
    private int sneakerId;
    private Database database;

    public UpdateDelete(int sneakerId) {
        database = new Database();

        this.sneakerId = sneakerId;
        Pane container = new Pane();
        container.setId("container");

        // Add navbar and input fields to the container
        container.getChildren().addAll(getNavBar(), getInput());

        // Set the scene
        updateDeleteScene = new Scene(container);
        container.requestFocus();
        updateDeleteScene.getStylesheets().add(Application.class.getResource("stylesheets/UpdateDelete.css").toString());
    }

    // Get the inputs
    public Pane getInput() {
        String imageInput = "";
        String brandInput = "";
        String modelInput = "";
        String sizeInput = "";
        String releaseDateInput = "";
        String purchaseDateInput = "";
        String priceInput = "";

        ResultSet sneakerResult = database.getSneakerById(sneakerId);
        try {
            if (sneakerResult.next()) {
                imageInput = sneakerResult.getString("image");
                brandInput = sneakerResult.getString("brand");
                modelInput = sneakerResult.getString("model");
                sizeInput = sneakerResult.getString("size");
                releaseDateInput = sneakerResult.getString("release_date");
                purchaseDateInput = sneakerResult.getString("purchase_date");
                priceInput = sneakerResult.getString("price");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Create VBox for input fields
        VBox inputFields = new VBox(20);
        inputFields.setPadding(new Insets(50));
        inputFields.relocate((applicationSize[0]-getNavBar().getPrefWidth())/2, 190);
        inputFields.setId("inputfields");

        // Create input fields
        TextField image = new TextField(imageInput);
        image.setPromptText("Image URL");
        image.setId("input");
        image.setPrefWidth(400);

        TextField brand = new TextField(brandInput);
        brand.setPromptText("Brand");
        brand.setId("input");
        brand.setPrefWidth(400);

        TextField model = new TextField(modelInput);
        model.setPromptText("Model");
        model.setId("input");
        model.setPrefWidth(400);

        TextField size = new TextField(sizeInput);
        size.setPromptText("Size");
        size.setId("input");
        size.setPrefWidth(400);

        DatePicker releaseDate = new DatePicker();
        releaseDate.setValue(LocalDate.parse(releaseDateInput));
        releaseDate.setPromptText("Release date");
        releaseDate.setId("input");
        releaseDate.setPrefWidth(400);
        releaseDate.setEditable(false);

        DatePicker purchaseDate = new DatePicker();
        purchaseDate.setValue(LocalDate.parse(purchaseDateInput));
        purchaseDate.setPromptText("Purchase date");
        purchaseDate.setId("input");
        purchaseDate.setPrefWidth(400);
        purchaseDate.setEditable(false);

        TextField price = new TextField(priceInput);
        price.setPromptText("Price");
        price.setId("input");
        price.setPrefWidth(400);

        Button updateButton = new Button("Update");
        updateButton.setId("button");
        updateButton.setPrefWidth(400);

        // Show confirmation dialog before updating
        updateButton.setOnAction(e -> {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Update Sneaker");
            confirmationDialog.setContentText("Are you sure you want to update this sneaker?");

            confirmationDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    // Pass sneakerId as the first argument
                    database.updateSneaker(sneakerId, image.getText(), brand.getText(), model.getText(),
                            size.getText(), releaseDate.getValue(), purchaseDate.getValue(), price.getText());
                    showCollection();
                }
            });
        });


        // Show confirmation dialog before deleting
        Button deleteButton = new Button("Delete");
        deleteButton.setId("button");
        deleteButton.setPrefWidth(400);
        deleteButton.setOnAction(e -> {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Delete Sneaker");
            confirmationDialog.setContentText("Are you sure you want to delete this sneaker?");

            confirmationDialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    database.deleteSneaker(sneakerId);
                    showCollection();
                }
            });
        });

        // Add input fields to the VBox
        inputFields.getChildren().addAll(image, brand, model, size, releaseDate, purchaseDate, price, updateButton, deleteButton);
        return inputFields;
    }

    // Show an alert
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    // Get nav bar
    public Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, Application.applicationSize[1]);
        navBar.setPadding(new Insets(40, 0, 0, 0));

        navBar.getChildren().addAll(
                generateNavItem("Collection", true, this::showCollection),
                generateNavItem("Add", false, this::showAdd),
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

    // Get the scene
    public Scene getUpdateDeleteScene() {
        return updateDeleteScene;
    }

    // Show the Collection screen
    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    // Show the Add screen
    private void showAdd() {
        Application.mainStage.setScene(scenes.get("Add"));
    }

    // Show the Statistics screen
    private void showStatistics() {
        scenes.put("Statistics", new Statistics().getStatisticsScene());
        Application.mainStage.setScene(scenes.get("Statistics"));
    }
}
