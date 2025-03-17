package nl.inholland.endassignment.endproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.inholland.endassignment.endproject.models.Showing;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditShowingController {

    @FXML
    private TextField titleField, startTimeField, endTimeField, seatsLeftField;
    @FXML
    private DatePicker startDatePicker, endDatePicker;

    private Showing showingToEdit;  // The showing that will be edited
    private boolean isSaved = false;

    public void setShowingToEdit(Showing showing) {
        this.showingToEdit = showing;

        // Populate fields with existing showing data
        titleField.setText(showing.getTitle());
        startDatePicker.setValue(showing.getStartDateTime().toLocalDate());
        startTimeField.setText(showing.getStartDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        endDatePicker.setValue(showing.getEndDateTime().toLocalDate());
        endTimeField.setText(showing.getEndDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        seatsLeftField.setText(String.valueOf(showing.getAvailableSeats()));
    }

    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void onSaveShowing() {
        try {
            // Get data from the form
            String title = titleField.getText().trim();
            LocalDateTime startDateTime = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"))
            );
            LocalDateTime endDateTime = LocalDateTime.of(
                    endDatePicker.getValue(),
                    LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"))
            );

            if (endDateTime.isBefore(startDateTime)) {
                showAlert("Invalid Input", "End date/time must be after start date/time.");
                return;
            }

            // Only update the total seats if a valid number is entered
            try {
                int totalSeats = Integer.parseInt(seatsLeftField.getText().trim());
                if (totalSeats < showingToEdit.getSoldSeats().size()) {
                    showAlert("Invalid Input", "Total seats cannot be less than the number of sold seats.");
                    return;
                }
                showingToEdit.setTotalSeats(totalSeats);
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Seats must be a valid number.");
                return;
            }

            // Update the showing details
            showingToEdit.setTitle(title);
            showingToEdit.setStartDateTime(startDateTime);
            showingToEdit.setEndDateTime(endDateTime);

            isSaved = true; // Mark as saved
            closeWindow(); // Close the dialog
        } catch (Exception e) {
            showAlert("Invalid Input", "Please fill in all fields correctly.");
        }
    }



    @FXML
    private void onCancel() {
        closeWindow();  // Close without saving
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
