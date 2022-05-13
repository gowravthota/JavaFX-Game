module com.example.cs2340 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cs2340 to javafx.fxml;
    exports com.example.cs2340;
}