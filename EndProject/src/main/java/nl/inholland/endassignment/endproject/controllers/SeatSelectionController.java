package nl.inholland.endassignment.endproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import nl.inholland.endassignment.endproject.models.Sale;
import nl.inholland.endassignment.endproject.models.Showing;
import nl.inholland.endassignment.endproject.utils.Database;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

public class SeatSelectionController {

    @FXML
    private GridPane seatGrid;

    @FXML
    private Label errorLabel;

    @FXML
    private ListView<String> selectedSeatsList;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField customerEmailField; // Added field for email

    @FXML
    private Button confirmButton;

    private Showing showing;
    private Database database;
    private final Set<Integer> selectedSeats = new HashSet<>();

    public void setShowing(Showing showing, Database database) {
        this.showing = showing;
        this.database = database;
        initializeSeatGrid();
    }

    private void initializeSeatGrid() {
        seatGrid.getChildren().clear();
        int rows = 6;
        int cols = 12;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int seatNumber = (row * cols) + col + 1;
                Button seatButton = new Button(String.valueOf(seatNumber));
                seatButton.setMinSize(40, 40);

                if (showing.getSoldSeats().contains(seatNumber)) {
                    seatButton.setStyle("-fx-background-color: red;");
                    seatButton.setDisable(true);
                } else {
                    seatButton.setStyle("-fx-background-color: lightgray;");
                    seatButton.setOnAction(event -> onSelectSeat(seatButton, seatNumber));
                }

                seatGrid.add(seatButton, col, row);
            }
        }

        // Add listeners to enable the confirm button based on input
        customerNameField.textProperty().addListener((observable, oldValue, newValue) -> updateConfirmButton());
        customerEmailField.textProperty().addListener((observable, oldValue, newValue) -> updateConfirmButton());
    }

    private void onSelectSeat(Button seatButton, int seatNumber) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            seatButton.setStyle("-fx-background-color: lightgray;");
        } else {
            selectedSeats.add(seatNumber);
            seatButton.setStyle("-fx-background-color: green;");
        }

        updateSelectedSeatsList();
        updateConfirmButton();
    }

    private void updateSelectedSeatsList() {
        List<String> seatDescriptions = new ArrayList<>();
        for (int seat : selectedSeats) {
            int row = (seat - 1) / 12 + 1;
            int column = (seat - 1) % 12 + 1;
            seatDescriptions.add("Row " + row + " / Seat " + column);
        }
        selectedSeatsList.getItems().setAll(seatDescriptions);
    }

    private void updateConfirmButton() {
        String customerName = customerNameField.getText().trim();
        int selectedCount = selectedSeats.size();

        // Update the button text
        confirmButton.setText("Sell " + selectedCount + " ticket" + (selectedCount == 1 ? "" : "s"));

        // Disable the button if the name is missing or no seats are selected
        confirmButton.setDisable(selectedCount == 0 || customerName.isEmpty());

        // Update error label
        if (customerName.isEmpty() && selectedCount > 0) {
            errorLabel.setText("Customer name is required.");
        } else {
            errorLabel.setText(""); // Clear error if valid
        }
    }

    @FXML
    private void onConfirm() {
        String customerName = customerNameField.getText().trim();
        if (selectedSeats.isEmpty() || customerName.isEmpty()) {
            errorLabel.setText("Customer name is required."); // Display error
            return;
        }

        String customerEmail = customerEmailField.getText().trim(); // Optional email

        for (int seatNumber : selectedSeats) {
            showing.sellSeat(seatNumber);
        }

        Sale sale = new Sale(
                customerName,
                customerEmail.isEmpty() ? null : customerEmail, // Optional email
                showing,
                List.copyOf(selectedSeats),
                LocalDateTime.now()
        );

        database.addSale(sale);
        closeWindow();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        confirmButton.getScene().getWindow().hide();
    }
}
