package nl.inholland.endassignment.endproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.inholland.endassignment.endproject.models.Showing;
import nl.inholland.endassignment.endproject.utils.Database;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddShowingController {

    @FXML
    private TextField titleField;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private TextField startTimeField, endTimeField;

    private Database database;
    private Runnable onCloseCallback;

    public void setDatabase(Database database, Runnable onCloseCallback) {
        this.database = database;
        this.onCloseCallback = onCloseCallback;
    }

    @FXML
    public void onAdd() {
        String title = titleField.getText();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        int seats = 72; // Default seats to 72

        if (title.isEmpty() || startDatePicker.getValue() == null || endDatePicker.getValue() == null ||
                startTime.isEmpty() || endTime.isEmpty()) {
            showAlert("Error", "All fields are required");
            return;
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startLocalTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endLocalTime);

            if (endDateTime.isBefore(startDateTime)) {
                showAlert("Error", "End date/time must be after start date/time");
                return;
            }

            // Add new showing with default 72 seats
            database.addShowing(new Showing(title, startDateTime, endDateTime, seats));
            closeWindow();
        } catch (Exception e) {
            showAlert("Invalid Input", "Time must be in the format HH:mm (e.g., 14:30)");
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
        if (onCloseCallback != null) {
            onCloseCallback.run(); // Refresh the main table after closing
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
