package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.Sneaker;
import com.example.sneakerapplication.classes.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Collection {
    private Scene collectionScene;
    private FlowPane sneakerSection, sneakers;
    private Sneaker sneaker;

        public Collection() {
            Pane container = new Pane();
            container.setId("container");

            sneaker = new Sneaker("", "", "", "", "", "", "", "");

            sneakerSection = new FlowPane();
            sneakerSection.setPrefSize(Application.applicationSize[0] - 165, Application.applicationSize[1] - 60);
            sneakerSection.setPadding(new Insets(80, 20, 20, 20));
            sneakerSection.relocate(165, 40);
            sneakerSection.setVgap(20);

            sneakers = new FlowPane();
            sneakers.setPrefSize(sneakerSection.getPrefWidth() - 40, sneakerSection.getPrefHeight());
            sneakers.setHgap(40);
            sneakers.setVgap(20);


            sneakerSection.getChildren().addAll(sneakers);

            container.getChildren().addAll(getNavBar(), sneakerSection);


            collectionScene = new Scene(container);
            collectionScene.getStylesheets().add(Application.class.getResource("stylesheets/collection.css").toString());


            Platform.runLater(this::getSneakers);
        }

    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(165, Application.applicationSize[1]);
        navBar.setPadding(new Insets(80, 0, 0, 0));
        navBar.getChildren().addAll(
                generateNavItem("Collection", true),
                generateNavItem("Add", false),
                generateNavItem("Statistics", false));
        return navBar;
    }

    private FlowPane generateNavItem(String title, boolean active) {
        FlowPane navItem = new FlowPane();
        navItem.setStyle(active ? "-fx-background-color: #F7F7F7;" : "");
        navItem.setPadding(new Insets(0, 0, 0, 20));
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setPrefSize(165, 35);
        navItem.setHgap(40);

        Text navItemText = new Text(title);
        navItemText.setStyle((active ? "-fx-fill: #3375CC;" : "-fx-fill: #FFFFFF;"));
        navItem.getChildren().addAll(navItemText);

        if (active) {
            ImageView arrow = new ImageView();
            arrow.setFitWidth(25);
            arrow.setPreserveRatio(true);
            arrow.setImage(new Image(Application.class.getResource("images/arrow_right.png").toString()));
            navItem.getChildren().add(arrow);
        }

        return navItem;
    }

    public FlowPane generateSneakerItem(Sneaker sneaker, Model model, Brand brand) {
        FlowPane sneakerItem = new FlowPane();
        sneakerItem.setOrientation(Orientation.HORIZONTAL);
        sneakerItem.setMaxSize(200, 250);
        sneakerItem.setVgap(15);
        sneakerItem.setStyle("-fx-background-color: pink;");

        Pane sneakerImage = new Pane();
        sneakerImage.setPrefSize(200, 200);
        ImageView sneakerImageView = new ImageView();
        sneakerImageView.setImage(new Image(sneaker.getImage()));
        sneakerImageView.setFitHeight(200);
        sneakerImageView.setFitWidth(200);


        sneakerImage.getChildren().add(sneakerImageView);


        FlowPane sneakerInfo = new FlowPane();
        sneakerInfo.setStyle("-fx-background-color: lightblue;");
        sneakerInfo.setOrientation(Orientation.VERTICAL);
        sneakerInfo.setPrefSize(200, 110);

        Text brand_id = new Text("Brand: " + brand.getBrand());
        brand_id.setId("brand_id");

        Text model_id = new Text("Model: " + model.getModel());
        model_id.setId("model_id");

        Text size = new Text("Size: " + sneaker.getSize());
        size.setId("size");

        Text release_date = new Text("Release Date: " + sneaker.getRelease_date());
        release_date.setId("release_date");

        Text purchase_date = new Text("Purchase Date: " + sneaker.getPurchase_date());
        purchase_date.setId("purchase_date");

        Text price = new Text("Price: €" + sneaker.getPrice());
        price.setId("price");

        sneakerInfo.getChildren().addAll(brand_id, model_id, size, release_date, purchase_date, price);
        sneakerItem.getChildren().addAll(sneakerImage, sneakerInfo);

        return sneakerItem;
    }

    private void getSneakers() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query = "SELECT * " +
                        "FROM sneaker s " +
                        "JOIN model m ON s.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE s.user_id = ?";


                ResultSet sneakerResult = Application.connection.query(query, loggedInUser.getUser_id());

                while (sneakerResult.next()) {
                    Sneaker sneaker = new Sneaker(sneakerResult);
                    Model model = new Model(sneakerResult);
                    Brand brand = new Brand(sneakerResult);

                    Node sneakerItem = generateSneakerItem(sneaker, model, brand);

                    if (!sneakers.getChildren().contains(sneakerItem)) {
                        sneakers.getChildren().add(sneakerItem);
                    }
                }
                sneakerSection.getChildren().setAll(sneakers);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Scene getCollectionScene() {
        return collectionScene;
    }
}
