package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.Sneaker;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.time.LocalDate;

import static com.example.sneakerapplication.Application.*;

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
        addButton.setOnAction(e -> {
            if (!image.getText().isEmpty() && !brand.getText().isEmpty() && !model.getText().isEmpty()
                    && !size.getText().isEmpty() && !release_date.getText().isEmpty()
                    && !purchase_date.getText().isEmpty() && !price.getText().isEmpty()) {
                addSneaker(image.getText(), brand.getText(), model.getText(),
                        size.getText(), release_date.getText(), purchase_date.getText(), price.getText());
                showCollection();
            } else {
                showAlert("Please fill in all fields.");
            }
        });

        inputFields.getChildren().addAll(image, brand, model, size, release_date, purchase_date, price, addButton);
        container.getChildren().addAll(getNavBar(),inputFields);


        addScene = new Scene(container);
        addScene.getStylesheets().add(Application.class.getResource("stylesheets/add.css").toString());
    }

    private void addSneaker(String image, String brandName, String modelName,
                            String size, String releaseDate, String purchaseDate, String price) {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {
                Brand brand = addBrand(brandName);

                Model model = addModel(modelName, brand);

                String insertQuery = "INSERT INTO sneaker (image, user_id, model_id, size, release_date, purchase_date, price) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                Application.connection.updateQuery(insertQuery, image, loggedInUser.getUser_id(), model.getModel_id(), size, releaseDate, purchaseDate, price);

            } else {
                showAlert("User not logged in.");
            }
        } catch (SQLException e) {
            showAlert("Error adding sneaker: " + e.getMessage());
        }
    }

    private Brand addBrand(String brandName) throws SQLException {
        String brandQuery = "SELECT * FROM brand WHERE brand = ?";
        ResultSet brandResult = Application.connection.query(brandQuery, brandName);

        if (brandResult.next()) {
            return new Brand(brandResult);
        } else {
            String insertBrandQuery = "INSERT INTO brand (brand) VALUES (?)";
            Application.connection.updateQuery(insertBrandQuery, brandName);

            ResultSet insertedBrandResult = Application.connection.query(brandQuery, brandName);
            insertedBrandResult.next();
            return new Brand(insertedBrandResult);
        }
    }

    private Model addModel(String modelName, Brand brand) throws SQLException {
        String modelQuery = "SELECT * FROM model WHERE model = ? AND brand_id = ?";
        ResultSet modelResult = Application.connection.query(modelQuery, modelName, brand.getBrand_id());

        if (modelResult.next()) {
            return new Model(modelResult);
        } else {
            String insertModelQuery = "INSERT INTO model (model, brand_id) VALUES (?, ?)";
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
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

//    private void showStatistics() {
//        Application.mainStage.setScene(scenes.get("Statistics"));
//    }
}
