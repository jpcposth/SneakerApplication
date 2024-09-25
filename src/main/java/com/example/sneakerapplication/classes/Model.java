package com.example.sneakerapplication.classes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Model {
    private final String model;
    private final int model_id, brand_id;

    public Model(int model_id, String model, int brand_id) {
        this.model_id = model_id;
        this.model = model;
        this.brand_id = brand_id;
    }

    public Model(ResultSet result) throws SQLException {
        this.model_id = result.getInt("model_id");
        this.model = result.getString("model");
        this.brand_id = result.getInt("brand_id");
    }

    public int getModel_id() {
        return model_id;
    }

    public String getModel() {
        return model;
    }
}
