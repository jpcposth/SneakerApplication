package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.MySQLConnection;
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

import java.sql.*;

import static com.example.sneakerapplication.Application.*;

public class Add {
    private Scene addScene;
    public Add() {
        Pane container = new Pane();
        container.setId("container");


        container.getChildren().addAll(getNavBar(),getInput());

        addScene = new Scene(container);
        container.requestFocus();
        addScene.getStylesheets().add(Application.class.getResource("stylesheets/Add.css").toString());
    }

    public Pane getInput() {
        VBox inputFields = new VBox(20);
        inputFields.setPadding(new Insets(50));;
        inputFields.relocate((applicationSize[0]-getNavBar().getPrefWidth())/2, 210);
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
        addButton.setOnAction(e -> {
            if (!image.getText().isEmpty() && !brand.getText().isEmpty() && !model.getText().isEmpty()
                    && !size.getText().isEmpty() && releaseDate.getValue() != null
                    && purchaseDate.getValue() != null && !price.getText().isEmpty()) {
                addSneaker(image.getText(), brand.getText(), model.getText(),
                        size.getText(), releaseDate.getValue().toString(), purchaseDate.getValue().toString(), price.getText());
                showCollection();
            } else {
                showAlert("Please fill in all fields.");
            }
        });
        inputFields.getChildren().addAll(image, brand, model, size, releaseDate, purchaseDate, price, addButton);
        return inputFields;
    }

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

    public void addSneaker(String image, String brandName, String modelName, String size, String releaseDate, String purchaseDate, String price) {
        try {
            User loggedInUser = Application.getLoggedInUser();
            if (loggedInUser != null) {
                Brand brand = addBrand(brandName);

                Model model = addModel(modelName, brand);

                String insertQuery =
                        "INSERT INTO sneaker (image, user_id, model_id, size, release_date, purchase_date, price) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                connection.updateQuery(insertQuery, image, loggedInUser.getUser_id(), model.getModel_id(), size, releaseDate, purchaseDate, price);

            }
        } catch (SQLException e) {
            showAlert("Please fill in all fields correctly");
        }
    }

    public  Brand addBrand(String brandName) throws SQLException {
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

    public Model addModel(String modelName, Brand brand) throws SQLException {
        String modelQuery =
                "SELECT * " +
                "FROM model " +
                "WHERE model = ? AND brand_id = ?";
        ResultSet modelResult = connection.query(modelQuery, modelName, brand.getBrand_id());

        if (modelResult.next()) {
            return new Model(modelResult);
        } else {
            String insertModelQuery =
                    "INSERT INTO model " +
                    "(model, brand_id) VALUES (?, ?)";
            connection.updateQuery(insertModelQuery, modelName, brand.getBrand_id());

            ResultSet insertedModelResult = connection.query(modelQuery, modelName, brand.getBrand_id());
            insertedModelResult.next();
            return new Model(insertedModelResult);
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public Scene getAddScene() {
        return addScene;
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    private void showStatistics() {
        scenes.put("Statistics", new Statistics().getStatisticsScene());
        Application.mainStage.setScene(scenes.get("Statistics"));
    }
}
