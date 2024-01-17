package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Brand {
    private final String brand_id, brand;

    public Brand(String brand_id, String brand) {
        this.brand_id = brand_id;
        this.brand = brand;
    }

    public Brand(ResultSet result) throws SQLException {
        this.brand_id = result.getString("brand_id");
        this.brand = result.getString("brand");
    }

    public String getBrand_id() {
        return brand_id;
    }

    public String getBrand() {
        return brand;
    }
}

