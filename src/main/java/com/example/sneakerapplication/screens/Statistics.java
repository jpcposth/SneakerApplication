package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.Database;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.*;

public class Statistics {
    private Scene statisticsScene;
    private Database database;

    public Statistics() {
        database = new Database();
        Pane container = new Pane();
        container.setId("container");

        try {
            // Add navbar and statistics to the container
            container.getChildren().addAll(getNavBar(), getStatistics());
        } catch (SQLException e) {
            e.printStackTrace(); // You can also show an error message to the user
        }

        // Set the scene
        statisticsScene = new Scene(container);
        statisticsScene.getStylesheets().add(Application.class.getResource("stylesheets/Statistics.css").toString());
    }

    public Pane getStatistics() throws SQLException {
        // Create HBox for statistics
        HBox statistics = new HBox();
        statistics.setSpacing(175);

        // Create left and right VBox for statistics
        VBox leftStatistics = new VBox(25);
        leftStatistics.setPadding(new Insets(50));
        leftStatistics.setId("statistics");

        VBox rightStatistics = new VBox(25);
        rightStatistics.setPadding(new Insets(50));
        rightStatistics.setId("statistics");

        // Create Text for statistics
        Text totalPrice = new Text(String.format("Total sneaker price: €%.2f", database.getTotalPrice(Application.getLoggedInUser())));
        totalPrice.setId("statistics-text");
        Text totalSneakers = new Text("Total sneakers amount: " + database.getTotalSneakers(Application.getLoggedInUser()));
        totalSneakers.setId("statistics-text");
        Text brandWithMostSneakers = new Text("The brand with the most sneakers: " + database.getBrandWithMostSneakers(Application.getLoggedInUser())[0]);
        Text brandWithMostSneakersAmount = new Text("Total sneaker amount from " + database.getBrandWithMostSneakers(Application.getLoggedInUser())[0] + ": " + database.getBrandWithMostSneakers(Application.getLoggedInUser())[1]);

        // Add statistics to the VBox
        leftStatistics.getChildren().addAll(totalPrice, totalSneakers);
        rightStatistics.getChildren().addAll(brandWithMostSneakers, brandWithMostSneakersAmount);

        // Add VBox to HBox
        statistics.getChildren().addAll(leftStatistics, rightStatistics);
        statistics.relocate(425, 100);

        return statistics;
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
                generateNavItem("Add", false, this::showAdd),
                generateNavItem("Statistics", true, () -> {}));
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

    // Get statistics scene
    public Scene getStatisticsScene() {
        return statisticsScene;
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
}
