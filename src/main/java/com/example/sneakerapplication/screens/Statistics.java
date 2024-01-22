package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
import com.example.sneakerapplication.classes.User;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.sneakerapplication.Application.applicationSize;
import static com.example.sneakerapplication.Application.scenes;

public class Statistics {
    private Scene statisticsScene;

    public Statistics() {
        Pane container = new Pane();
        container.setId("container");

        VBox statistics = new VBox();
        statistics.setAlignment(Pos.CENTER);
        statistics.setPadding(new Insets(50));
        statistics.relocate(applicationSize[0] / 2 - 550, applicationSize[1] / 2 - 475);
        statistics.setId("statistics");

        Text total_price = new Text(String.format("Total sneaker price: €%.2f", getTotalPrice()));

        Text total_sneakers = new Text("Total sneakers amount: " + getTotalSneakers());

        statistics.getChildren().addAll(total_price,total_sneakers);
        container.getChildren().addAll(getNavBar(), statistics);

        statisticsScene = new Scene(container);
        statisticsScene.getStylesheets().add(Application.class.getResource("stylesheets/statistics.css").toString());
    }

    private double getTotalPrice() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query =
                        "SELECT SUM(price) AS total_price " +
                        "FROM sneaker " +
                        "WHERE user_id = ?";
                ResultSet resultSet = Application.connection.query(query, loggedInUser.getUser_id());

                if (resultSet.next()) {
                    return resultSet.getDouble("total_price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private int getTotalSneakers() {
        try {
            User loggedInUser = Application.getLoggedInUser();

            if (loggedInUser != null) {
                String query =
                        "SELECT COUNT(sneaker_id) AS total_sneakers " +
                        "FROM sneaker " +
                        "WHERE user_id = ?";
                ResultSet resultSet = Application.connection.query(query, loggedInUser.getUser_id());

                if (resultSet.next()) {
                    return resultSet.getInt("total_sneakers");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Pane getNavBar() {
        FlowPane navBar = new FlowPane();
        navBar.setId("navbar");
        navBar.setOrientation(Orientation.HORIZONTAL);
        navBar.setPrefSize(250, applicationSize[1]);
        navBar.setPadding(new Insets(80, 0, 0, 0));
        navBar.getChildren().addAll(
                generateNavItem("Collection", false, this::showCollection),
                generateNavItem("Add", false, this::showAdd),
                generateNavItem("Statistics", true, () -> {}));
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

    public Scene getStatisticsScene() {
        return statisticsScene;
    }

    private void showCollection() {
        scenes.put("Collection", new Collection().getCollectionScene());
        Application.mainStage.setScene(scenes.get("Collection"));
    }

    private void showAdd() {
        Application.mainStage.setScene(scenes.get("Add"));
    }
}
