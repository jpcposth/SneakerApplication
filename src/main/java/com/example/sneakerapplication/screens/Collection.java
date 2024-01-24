package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.Brand;
import com.example.sneakerapplication.classes.Model;
import com.example.sneakerapplication.classes.Sneaker;
import com.example.sneakerapplication.classes.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.sql.PreparedStatement;
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
    private ComboBox<String> comboBoxBrand;

    public Collection() {
        FlowPane container = new FlowPane(0, 0);
        container.setId("container");

        sneaker = new Sneaker("", "", "", "", "", "", "", "");

        container.getChildren().addAll(getNavBar(), getCollection());

        collectionScene = new Scene(container);
        collectionScene.getStylesheets().add(Application.class.getResource("stylesheets/collection.css").toString());

        Platform.runLater(() -> {
            getDistinctBrands();
            getSneakers();
        });
//        long startTime = System.currentTimeMillis();
//        Platform.runLater(() -> {
//            long endTime = System.currentTimeMillis();
//            long loadTimeMillis = endTime - startTime;
//            double loadTimeSeconds = loadTimeMillis / 1000.0;
//            System.out.println("Sneakers load time: " + loadTimeSeconds + " seconds");
//
//        });
    }

    private ScrollPane getCollection() {
        sneakerSection = new FlowPane();
        sneakerSection.setPadding(new Insets(40, 0, 40, 50));
        sneakerSection.setId("sneaker_section");
        sneakerSection.setPrefSize(applicationSize [0]-getNavBar().getPrefWidth()-15, applicationSize [1]-37);
        sneakerSection.setVgap(40);

        sneakers = new TilePane();
        sneakers.setHgap(50);
        sneakers.setVgap(40);
        sneakers.setPrefColumns(4);

        comboBoxBrand = new ComboBox<>();
        comboBoxBrand.setPromptText("Brand");
        comboBoxBrand.setPrefWidth(250);
        comboBoxBrand.setId("combo");
        comboBoxBrand.setVisibleRowCount(5);

        pi = new ProgressIndicator();
        pi.setMinWidth(applicationSize [0]-getNavBar().getPrefWidth());

        sneakerSection.getChildren().addAll(pi);

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

        Text brandText = new Text("Brand: " + brand.getBrand());
        brandText.setId("brand_id");

        Text modelText = new Text("Model: " + model.getModel());
        modelText.setId("model_id");

        double sizeValue = Double.parseDouble(sneaker.getSize());
        Text size = new Text(String.format("Size: %.1f", sizeValue));
        size.setId("size");

        LocalDate release_date = LocalDate.parse(sneaker.getRelease_date());
        Text releaseDate = new Text("Release Date: " + release_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        releaseDate.setId("release_date");

        LocalDate purchase_date = LocalDate.parse(sneaker.getPurchase_date());
        Text purchaseDate = new Text("Purchase Date: " + purchase_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        purchaseDate.setId("purchase_date");

        double priceValue = Double.parseDouble(sneaker.getPrice());
        Text price = new Text(String.format("Price: €%.2f", priceValue));
        price.setId("price");

        sneakerInfo.getChildren().addAll(brandText, modelText, size, releaseDate, purchaseDate, price);
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
                        sneakerResult = Application.connection.query(query, loggedInUser.getUser_id(), comboBoxBrand.getValue());
                    }else {
                        sneakerResult = Application.connection.query(query, loggedInUser.getUser_id());
                    }
                }else {
                    sneakerResult = Application.connection.query(query, loggedInUser.getUser_id());
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
                    sneakerSection.getChildren().remove(pi);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

                ResultSet brandResult = Application.connection.query(query, loggedInUser.getUser_id());

                ObservableList<String> brandList = FXCollections.observableArrayList();
                brandList.add("All");

                while (brandResult.next()) {
                    String brand = brandResult.getString("brand");
                    brandList.add(brand);
                }

                Platform.runLater(() -> {
                    comboBoxBrand.setItems(brandList);
                    comboBoxBrand.setOnAction(event -> {
                        getSneakers();
                    });
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void showUpdateDelete(int sneakerId) {
        UpdateDelete updateDeleteScreen = new UpdateDelete(sneakerId);
        scenes.put("UpdateDelete", updateDeleteScreen.getUpdateScene());
        Application.mainStage.setScene(scenes.get("UpdateDelete"));
    }
}
