module com.example.sneakerapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.sneakerapplication to javafx.fxml;
    exports com.example.sneakerapplication;
}