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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.sneakerapplication.Application.applicationSize;
import static com.example.sneakerapplication.Application.scenes;

public class Collection {
    private Scene collectionScene;
    private FlowPane sneakerSection;
    private TilePane sneakers;
    private Sneaker sneaker;
    private ProgressIndicator pi;

    public Collection() {
        FlowPane container = new FlowPane(0, 0);
        container.setId("container");

        sneaker = new Sneaker("", "", "", "", "", "", "", "");
        
        container.getChildren().addAll(getNavBar(), getCollection());

        collectionScene = new Scene(container);
        collectionScene.getStylesheets().add(Application.class.getResource("stylesheets/collection.css").toString());

        Platform.runLater(this::getSneakers);
    }

    private ScrollPane getCollection() {
        sneakerSection = new FlowPane();
        sneakerSection.setPadding(new Insets(80, 0, 0, 64));

        sneakers = new TilePane();
        sneakers.setHgap(64);
        sneakers.setVgap(64);
        sneakers.setPrefColumns(3);

        pi = new ProgressIndicator();
        pi.setMinWidth(1016);

        sneakerSection.getChildren().addAll(pi, sneakers);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(sneakerSection);
        scrollPane.setPrefSize(1015, 683);

        return scrollPane;
    }

    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, 683);
        navBar.setPadding(new Insets(80, 0, 0, 0));

        navBar.getChildren().addAll(
                generateNavItem("Collection", true, () -> {}),
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

    public FlowPane generateSneakerItem(Sneaker sneaker, Model model, Brand brand) {
        FlowPane sneakerItem = new FlowPane();
        sneakerItem.setOrientation(Orientation.HORIZONTAL);
        sneakerItem.setMaxSize(250, 350);
//        sneakerItem.setStyle("-fx-background-color: pink;");

        FlowPane sneakerImage = new FlowPane();
        sneakerImage.setPrefSize(250, 250);
        ImageView sneakerImageView = new ImageView();
        sneakerImageView.setImage(new Image(sneaker.getImage()));
        sneakerImageView.setPreserveRatio(true);
        sneakerImageView.setFitWidth(250);

        sneakerImage.getChildren().add(sneakerImageView);

        FlowPane sneakerInfo = new FlowPane();
        sneakerInfo.setOrientation(Orientation.VERTICAL);
        sneakerInfo.setPrefSize(250, 150);

        Text brand_id = new Text("Brand: " + brand.getBrand());
        brand_id.setId("brand_id");

        Text model_id = new Text("Model: " + model.getModel());
        model_id.setId("model_id");

        double sizeValue = Double.parseDouble(sneaker.getSize());
        Text size = new Text(String.format("Size: %.1f", sizeValue));
        size.setId("size");

        LocalDate releaseDate = LocalDate.parse(sneaker.getRelease_date());
        Text release_date = new Text("Release Date: " + releaseDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        release_date.setId("release_date");

        LocalDate purchaseDate = LocalDate.parse(sneaker.getPurchase_date());
        Text purchase_date = new Text("Purchase Date: " + purchaseDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        purchase_date.setId("purchase_date");

        double priceValue = Double.parseDouble(sneaker.getPrice());
        Text price = new Text(String.format("Price: €%.2f", priceValue));
        price.setId("price");

        sneakerInfo.getChildren().addAll(brand_id, model_id, size, release_date, purchase_date, price);
        sneakerInfo.setId("sneaker_info");
        sneakerItem.getChildren().addAll(sneakerImage, sneakerInfo);
        sneakerItem.setId("sneaker_item");

        return sneakerItem;
    }

    private void getSneakers() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query =
                        "SELECT * " +
                        "FROM sneaker s " +
                        "JOIN model m ON s.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE s.user_id = ? " +
                        "ORDER BY s.sneaker_id ASC;";

                ResultSet sneakerResult = Application.connection.query(query, loggedInUser.getUser_id());

                while (sneakerResult.next()) {
                    Sneaker sneaker = new Sneaker(sneakerResult);
                    Model model = new Model(sneakerResult);
                    Brand brand = new Brand(sneakerResult);

                    Node sneakerItem = generateSneakerItem(sneaker, model, brand);
                    sneakerItem.setOnMouseClicked(event -> {
                        try {
                            int sneakerId = Integer.parseInt(sneaker.getSneaker_id());
                            showUpdate(sneakerId);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    });

                    if (!sneakers.getChildren().contains(sneakerItem)) {
                        sneakers.getChildren().add(sneakerItem);
                    }
                }
                sneakerSection.getChildren().remove(pi);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Scene getCollectionScene() {
        return collectionScene;
    }
    private void showAdd() {
        Application.mainStage.setScene(scenes.get("Add"));
    }

    private void showStatistics() {
        scenes.put("Statistics", new Statistics().getStatisticsScene());
        Application.mainStage.setScene(scenes.get("Statistics"));
    }

    private void showUpdate(int sneakerId) {
        UpdateDelete updateDeleteScreen = new UpdateDelete(sneakerId);
        scenes.put("Update", updateDeleteScreen.getUpdateScene());
        Application.mainStage.setScene(scenes.get("Update"));
    }
}
