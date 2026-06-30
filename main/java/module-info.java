module org.example.moodtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens org.example.moodtracker to javafx.fxml;
    opens org.example.moodtracker.controller to javafx.fxml;
    opens org.example.moodtracker.model to javafx.base;

    exports org.example.moodtracker;
}