package com.example.sneakerapplication.screens;

import com.example.sneakerapplication.Application;
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

import static com.example.sneakerapplication.Application.applicationSize;
import static com.example.sneakerapplication.Application.scenes;

public class Statistics {
    private Scene statisticsScene;

    public Statistics() {
        Pane container = new Pane();
        container.setId("container");

        container.getChildren().addAll(getNavBar(), getStatistics());

        statisticsScene = new Scene(container);
        statisticsScene.getStylesheets().add(Application.class.getResource("stylesheets/statistics.css").toString());
    }

    private Pane getStatistics() {
        HBox statistics = new HBox();
        statistics.setSpacing(175);

        VBox leftStatistics = new VBox(25);
        leftStatistics.setPadding(new Insets(50));
        leftStatistics.setId("statistics");

        VBox rightStatistics = new VBox(25);
        rightStatistics.setPadding(new Insets(50));
        rightStatistics.setId("statistics");

        Text totalPrice = new Text(String.format("Total sneaker price: €%.2f", getTotalPrice()));
        totalPrice.setId("statistics-text");
        Text totalSneakers = new Text("Total sneakers amount: " + getTotalSneakers());
        totalSneakers.setId("statistics-text");
        Text brandWithMostSneakers = new Text("The brand with the most sneakers: " + getBrandWithMostSneakers(Application.getLoggedInUser())[0]);
        Text brandWithMostSneakersAmount = new Text("Total sneaker amount from " + getBrandWithMostSneakers(Application.getLoggedInUser())[0] + ": " + getBrandWithMostSneakers(Application.getLoggedInUser())[1]);

        leftStatistics.getChildren().addAll(totalPrice, totalSneakers);
        rightStatistics.getChildren().addAll(brandWithMostSneakers, brandWithMostSneakersAmount);

        statistics.getChildren().addAll(leftStatistics, rightStatistics);
        statistics.relocate(425, 100);

        return statistics;
    }
    private String[] getBrandWithMostSneakers(User user) {
        try {
            if (user != null) {
                String query =
                        "SELECT brand.brand, COUNT(sneaker.sneaker_id) AS total_sneakers " +
                                "FROM brand " +
                                "JOIN model ON brand.brand_id = model.brand_id " +
                                "JOIN sneaker ON model.model_id = sneaker.model_id " +
                                "WHERE sneaker.user_id = ? " +
                                "GROUP BY brand.brand " +
                                "ORDER BY total_sneakers DESC " +
                                "LIMIT 1";

                ResultSet resultSet = Application.connection.query(query, user.getUser_id());

                if (resultSet.next()) {
                    String brandName = resultSet.getString("brand");
                    int totalSneakers = resultSet.getInt("total_sneakers");
                    return new String[]{brandName, String.valueOf(totalSneakers)};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"", "0"};
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
        navBar.setPadding(new Insets(40, 0, 0, 0));
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
