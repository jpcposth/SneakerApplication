package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
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

    public UpdateDelete(int sneakerId) {
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

        ResultSet sneakerResult = getSneaker();
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
                        updateSneaker(image.getText(), brand.getText(), model.getText(),
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
                    deleteSneaker();
                    showCollection();
                }
            });
        });

        // Add input fields to the VBox
        inputFields.getChildren().addAll(image, brand, model, size, releaseDate, purchaseDate, price, updateButton, deleteButton);
        return inputFields;
    }

    // Get the sneaker
    public ResultSet getSneaker() {
        ResultSet sneakerResult = null;

        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query =
                        "SELECT * " +
                        "FROM sneaker s " +
                        "JOIN model m ON s.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE s.sneaker_id = '" + this.sneakerId + "';";
                sneakerResult = connection.query(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sneakerResult;
    }

    // Delete a sneaker
    public void deleteSneaker() {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {

                String updateQuery =
                        "DELETE FROM sneaker " +
                        "WHERE sneaker_id = ?";

                connection.updateQuery(updateQuery, this.sneakerId);
            }
        } catch (SQLException e) {
            showAlert("Please fill in all fields correctly");
        }
    }

    // Update a sneaker
    public void updateSneaker(String image, String brandName, String modelName, String size, LocalDate releaseDate, LocalDate purchaseDate, String price) {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {
                Brand brand = updateBrand(brandName);
                Model model = updateModel(modelName, brand);

                String updateQuery =
                        "UPDATE sneaker " +
                        "SET image = ?, model_id = ?, size = ?, release_date = ?, purchase_date = ?, price = ? " +
                        "WHERE sneaker_id = ?";
                connection.updateQuery(updateQuery, image, model.getModel_id(), size, releaseDate, purchaseDate, price, this.sneakerId);
            }
        } catch (SQLException e) {
            showAlert("Please fill in all fields correctly");
        }
    }

    // Update brand
    public Brand updateBrand(String brandName) throws SQLException {
        String brandQuery =
                "SELECT * " +
                "FROM brand " +
                "WHERE brand = ?";
        ResultSet brandResult = connection.query(brandQuery, brandName);

        if (brandResult.next()) {
            return new Brand(brandResult);
        } else {
            String insertBrandQuery =
                    "INSERT INTO brand " +
                    "(brand) VALUES (?)";
            connection.updateQuery(insertBrandQuery, brandName);

            ResultSet insertedBrandResult = connection.query(brandQuery, brandName);
            insertedBrandResult.next();
            return new Brand(insertedBrandResult);
        }
    }

    // Update model
    public Model updateModel(String modelName, Brand brand) throws SQLException {
        String modelQuery =
                "SELECT * " +
                "FROM model " +
                "WHERE model = ? AND brand_id = ?";
        ResultSet modelResult = connection.query(modelQuery, modelName, brand.getBrand_id());

        if (modelResult.next()) {
            return new Model(modelResult);
        }
        else {
            String insertModelQuery =
                    "INSERT INTO model " +
                    "(model, brand_id) VALUES (?, ?)";
            connection.updateQuery(insertModelQuery, modelName, brand.getBrand_id());

            ResultSet insertedModelResult = connection.query(modelQuery, modelName, brand.getBrand_id());
            insertedModelResult.next();
            return new Model(insertedModelResult);
        }
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
