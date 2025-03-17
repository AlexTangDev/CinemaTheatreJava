module nl.inholland.endassignment.endproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    // Export controllers for FXML
    opens nl.inholland.endassignment.endproject.controllers to javafx.fxml;

    // Export models to JavaFX for reflection
    opens nl.inholland.endassignment.endproject.models to javafx.base;

    exports nl.inholland.endassignment.endproject;
}