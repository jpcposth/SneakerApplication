package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.Sneaker;
import com.example.sneakerapplication.classes.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.sneakerapplication.Application.*;

public class Collection {
    private Scene collectionScene;
    private FlowPane sneakerSection;
    private TilePane sneakers;
    private ComboBox<String> comboBoxBrand;

    public Collection() {
        FlowPane container = new FlowPane(0, 0);
        container.setId("container");

        // Add navbar and collection fields to the container
        container.getChildren().addAll(getNavBar(), getCollection());

        //
        Platform.runLater(() -> {
            getDistinctBrands();
            getSneakers();
        });

        // Set the scene
        collectionScene = new Scene(container);
        container.requestFocus();
        collectionScene.getStylesheets().add(Application.class.getResource("stylesheets/Collection.css").toString());
    }

    // Get the collection
    private ScrollPane getCollection() {
        // Create the sneaker section
        sneakerSection = new FlowPane();
        sneakerSection.setPadding(new Insets(40, 0, 40, 45));
        sneakerSection.setId("sneaker_section");
        sneakerSection.setPrefSize(applicationSize [0]-getNavBar().getPrefWidth()-15, applicationSize [1]-37);
        sneakerSection.setVgap(40);

        sneakers = new TilePane();
        sneakers.setHgap(45);
        sneakers.setVgap(45);
        sneakers.setPrefColumns(4);

        // Create the filter
        comboBoxBrand = new ComboBox<>();
        comboBoxBrand.setPromptText("Filter on brand");
        comboBoxBrand.setPrefWidth(260);
        comboBoxBrand.setId("combo");
        comboBoxBrand.setVisibleRowCount(5);

        Platform.runLater(() -> {
            sneakerSection.getChildren().addAll(comboBoxBrand, sneakers);
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(sneakerSection);
        scrollPane.setPrefSize(applicationSize [0]-getNavBar().getPrefWidth()-15, applicationSize [1]-37);

        return scrollPane;
    }

    // Get navbar
    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, applicationSize [1]-37);
        navBar.setPadding(new Insets(40, 0, 0, 0));

        navBar.getChildren().addAll(
                generateNavItem("Collection", true, () -> {}),
                generateNavItem("Add", false, this::showAdd),
                generateNavItem("Statistics", false, this::showStatistics));

        return navBar;
    }

    // Generate nav item
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

    // Generate sneaker item
    public FlowPane generateSneakerItem(Sneaker sneaker, Model model, Brand brand) {
        FlowPane sneakerItem = new FlowPane();
        sneakerItem.setOrientation(Orientation.HORIZONTAL);
        sneakerItem.setMaxSize(250, 250);
        sneakerItem.setId("sneaker_item");

        // Create a progress indicator
        ProgressIndicator pi = new ProgressIndicator();
        pi.setMinWidth(250);

        FlowPane sneakerImage = new FlowPane();
        sneakerImage.setPrefSize(250, 100);
        sneakerImage.getChildren().add(pi);

        ImageView sneakerImageView = new ImageView();
        sneakerImageView.setPreserveRatio(true);
        sneakerImageView.setFitWidth(250);

        // Load the sneaker image
        Service<Image> loadImageService = new Service<>() {
            @Override
            protected Task<Image> createTask() {
                return new Task<>() {
                    @Override
                    protected Image call() {
                        try {
                            Thread.sleep(1);
                            return new Image(sneaker.getImage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }
        };
        pi.progressProperty().bind(loadImageService.progressProperty());
        loadImageService.setOnSucceeded(event -> {
            sneakerImage.getChildren().remove(pi);
            sneakerImageView.setImage(loadImageService.getValue());
            sneakerImage.getChildren().add(sneakerImageView);
        });
        loadImageService.start();

        // Create the sneaker info
        FlowPane sneakerInfo = new FlowPane();
        sneakerInfo.setOrientation(Orientation.VERTICAL);
        sneakerInfo.setPrefSize(250, 136);

        Text brandText = new Text("Brand: " + brand.getBrand());

        Text modelText = new Text("Model: " + model.getModel());

        double sizeValue = Double.parseDouble(sneaker.getSize());
        Text size = new Text(String.format("Size: %.1f", sizeValue));

        LocalDate release_date = LocalDate.parse(sneaker.getRelease_date());
        Text releaseDate = new Text("Release Date: " + release_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        LocalDate purchase_date = LocalDate.parse(sneaker.getPurchase_date());
        Text purchaseDate = new Text("Purchase Date: " + purchase_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        double priceValue = Double.parseDouble(sneaker.getPrice());
        Text price = new Text(String.format("Price: €%.2f", priceValue));

        sneakerInfo.getChildren().addAll(brandText, modelText, size, releaseDate, purchaseDate, price);
        sneakerItem.getChildren().addAll(sneakerImage, sneakerInfo);

        return sneakerItem;
    }

    // Get all the sneakers from the database for the logged in user
    private void getSneakers() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {

                String query =
                        "SELECT * " +
                        "FROM sneaker s " +
                        "JOIN model m ON s.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE s.user_id = ?";

                if (comboBoxBrand.getValue() != null) {
                    if(comboBoxBrand.getValue().toString() != "All") {
                        query += " AND b.brand = ?";
                    }
                    sneakers.getChildren().clear();
                }

                query += " ORDER BY s.sneaker_id DESC;";

                ResultSet sneakerResult = null;
                if (comboBoxBrand.getValue() != null) {
                    if(!comboBoxBrand.getValue().toString().equals("All")) {
                        sneakerResult = connection.query(query, loggedInUser.getUser_id(), comboBoxBrand.getValue());
                    }else {
                        sneakerResult = connection.query(query, loggedInUser.getUser_id());
                    }
                }else {
                    sneakerResult = connection.query(query, loggedInUser.getUser_id());
                }

                if(sneakerResult != null) {
                    while (sneakerResult.next()) {
                        Sneaker sneaker = new Sneaker(sneakerResult);
                        Model model = new Model(sneakerResult);
                        Brand brand = new Brand(sneakerResult);

                        Node sneakerItem = generateSneakerItem(sneaker, model, brand);
                        sneakerItem.setOnMouseClicked(event -> {
                            try {
                                int sneakerId = Integer.parseInt(sneaker.getSneaker_id());
                                showUpdateDelete(sneakerId);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        });

                        if (!sneakers.getChildren().contains(sneakerItem)) {
                            sneakers.getChildren().add(sneakerItem);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get all the distinct brands from the database
    private void getDistinctBrands() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query =
                        "SELECT DISTINCT b.brand " +
                        "FROM sneaker s " +
                        "JOIN model m ON s.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE s.user_id = ? " +
                        "ORDER BY b.brand DESC;";

                ResultSet brandResult = connection.query(query, loggedInUser.getUser_id());

                ObservableList<String> brandList = FXCollections.observableArrayList();
                brandList.add("All");

                while (brandResult.next()) {
                    String brand = brandResult.getString("brand");
                    brandList.add(brand);
                }

                comboBoxBrand.setItems(brandList);
                comboBoxBrand.setOnAction(event -> {
                    getSneakers();
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Get the collection scene
    public Scene getCollectionScene() {
        return collectionScene;
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

    // Show the UpdateDelete screen
    private void showUpdateDelete(int sneakerId) {
        UpdateDelete updateDeleteScreen = new UpdateDelete(sneakerId);
        scenes.put("UpdateDelete", updateDeleteScreen.getUpdateDeleteScene());
        Application.mainStage.setScene(scenes.get("UpdateDelete"));
    }
}
