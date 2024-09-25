package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Brand {
    private final String brand;
    private final int brand_id;

    public Brand(int brand_id, String brand) {
        this.brand_id = brand_id;
        this.brand = brand;
    }

    public Brand(ResultSet result) throws SQLException {
        this.brand_id = result.getInt("brand_id");
        this.brand = result.getString("brand");
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getBrand() {
        return brand;
    }
}

