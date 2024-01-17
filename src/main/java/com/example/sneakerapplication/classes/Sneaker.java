package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Sneaker {
    private final String sneaker_id, image, user_id, model_id, size, release_date, purchase_date, price;

    public Sneaker(String sneaker_id, String image, String user_id, String model_id, String size, String release_date, String purchase_date, String price) {
        this.sneaker_id = sneaker_id;
        this.image = image;
        this.user_id = user_id;
        this.model_id = model_id;
        this.size = size;
        this.release_date = release_date;
        this.purchase_date = purchase_date;
        this.price = price;
    }

    public Sneaker(ResultSet result) throws SQLException {
        this.sneaker_id = result.getString("sneaker_id");
        this.image = result.getString("image");
        this.user_id = result.getString("user_id");
        this.model_id = result.getString("model_id");
        this.size = result.getString("size");
        this.release_date = result.getString("release_date");
        this.purchase_date = result.getString("purchase_date");
        this.price = result.getString("price");
    }

    public String getSneaker_id() {
        return sneaker_id;
    }

    public String getImage() {
        return image;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getModel_id() {
        return model_id;
    }

    public String getSize() {
        return size;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public String getPrice() {
        return price;
    }
}