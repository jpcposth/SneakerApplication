package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Model {
    private final String model_id, model, brand_id;

    public Model(String model_id, String model, String brand_id) {
        this.model_id = model_id;
        this.model = model;
        this.brand_id = brand_id;
    }

    public Model(ResultSet result) throws SQLException {
        this.model_id = result.getString("model_id");
        this.model = result.getString("model");
        this.brand_id = result.getString("brand_id");
    }

    public String getModel_id() {
        return model_id;
    }

    public String getModel() {
        return model;
    }

    public String getBrand_id() {
        return brand_id;
    }
}
