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
import java.time.format.DateTimeFormatter;

import static com.example.sneakerapplication.Application.applicationSize;
import static com.example.sneakerapplication.Application.scenes;

public class UpdateDelete {
    private Scene updateDeleteScene;
    private int sneakerId;

    public UpdateDelete(int sneakerId) {
        this.sneakerId = sneakerId;
        Pane container = new Pane();
        container.setId("container");

        container.getChildren().addAll(getNavBar(), getInput());

        updateDeleteScene = new Scene(container);
        updateDeleteScene.getStylesheets().add(Application.class.getResource("stylesheets/updatedelete.css").toString());
    }


    private Pane getInput() {
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

        VBox inputFields = new VBox(20);
        inputFields.setPadding(new Insets(50));
        inputFields.relocate((applicationSize[0]-getNavBar().getPrefWidth())/2, 190);
        inputFields.setId("inputfields");

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

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate releaseDate = LocalDate.parse(releaseDateInput, inputFormatter);
        String formattedReleaseDate = releaseDate.format(outputFormatter);
        DatePicker release_date = new DatePicker(LocalDate.parse(formattedReleaseDate, outputFormatter));
        release_date.setPromptText("Release Date");
        release_date.setId("input");
        release_date.setPrefWidth(400);

        LocalDate purchaseDate = LocalDate.parse(purchaseDateInput, inputFormatter);
        String formattedPurchaseDate = purchaseDate.format(outputFormatter);
        DatePicker purchase_date = new DatePicker(LocalDate.parse(formattedPurchaseDate, outputFormatter));
        purchase_date.setPromptText("Purchase Date");
        purchase_date.setId("input");
        purchase_date.setPrefWidth(400);

        TextField price = new TextField(priceInput);
        price.setPromptText("Price");
        price.setId("input");
        price.setPrefWidth(400);

        Button updateButton = new Button("Update");
        updateButton.setId("button");
        updateButton.setPrefWidth(400);
        updateButton.setOnAction(e -> {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Update Sneaker");
            confirmationDialog.setContentText("Are you sure you want to update this sneaker?");

            confirmationDialog.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        updateSneaker(image.getText(), brand.getText(), model.getText(),
                                size.getText(), release_date.getValue(), purchase_date.getValue(), price.getText());
                        showCollection();
                    }
            });
        });

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

        inputFields.getChildren().addAll(image, brand, model, size, release_date, purchase_date, price, updateButton, deleteButton);
        return inputFields;
    }

    private ResultSet getSneaker() {
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
                sneakerResult = Application.connection.query(query);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sneakerResult;
    }

    private void deleteSneaker() {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {

                String updateQuery =
                        "DELETE FROM sneaker " +
                        "WHERE sneaker_id = ?";

                Application.connection.updateQuery(updateQuery, this.sneakerId);
            }
        } catch (SQLException e) {
            showAlert("Error updating sneaker: " + e.getMessage());
        }
    }

    private void updateSneaker(String image, String brandName, String modelName, String size, LocalDate releaseDate, LocalDate purchaseDate, String price) {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {
                Brand brand = updateBrand(brandName);
                Model model = updateModel(modelName, brand);

                String updateQuery =
                        "UPDATE sneaker " +
                        "SET image = ?, model_id = ?, size = ?, release_date = ?, purchase_date = ?, price = ? " +
                        "WHERE sneaker_id = ?";
                Application.connection.updateQuery(updateQuery, image, model.getModel_id(), size, releaseDate, purchaseDate, price, this.sneakerId);
            }
        } catch (SQLException e) {
            showAlert("Error updating sneaker: " + e.getMessage());
        }
    }


    private Brand updateBrand(String brandName) throws SQLException {
        String brandQuery =
                "SELECT * " +
                "FROM brand " +
                "WHERE brand = ?";
        ResultSet brandResult = Application.connection.query(brandQuery, brandName);

        if (brandResult.next()) {
            return new Brand(brandResult);
        } else {
            String insertBrandQuery =
                    "INSERT INTO brand " +
                    "(brand) VALUES (?)";
            Application.connection.updateQuery(insertBrandQuery, brandName);

            ResultSet insertedBrandResult = Application.connection.query(brandQuery, brandName);
            insertedBrandResult.next();
            return new Brand(insertedBrandResult);
        }
    }

    private Model updateModel(String modelName, Brand brand) throws SQLException {
        String modelQuery =
                "SELECT * " +
                "FROM model " +
                "WHERE model = ? AND brand_id = ?";
        ResultSet modelResult = Application.connection.query(modelQuery, modelName, brand.getBrand_id());

        if (modelResult.next()) {
            return new Model(modelResult);
        }
        else {
            String insertModelQuery =
                    "INSERT INTO model " +
                    "(model, brand_id) VALUES (?, ?)";
            Application.connection.updateQuery(insertModelQuery, modelName, brand.getBrand_id());

            ResultSet insertedModelResult = Application.connection.query(modelQuery, modelName, brand.getBrand_id());
            insertedModelResult.next();
            return new Model(insertedModelResult);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private Pane getNavBar() {
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

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    private void showAdd() {
        Application.mainStage.setScene(scenes.get("Add"));
    }

    private void showStatistics() {
        scenes.put("Statistics", new Statistics().getStatisticsScene());
        Application.mainStage.setScene(scenes.get("Statistics"));
    }

    public Scene getUpdateDeleteScene() {
        return updateDeleteScene;
    }
}
