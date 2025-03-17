package nl.inholland.endassignment.endproject.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.inholland.endassignment.endproject.models.Showing;
import nl.inholland.endassignment.endproject.utils.Database;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesController {

    @FXML
    private TableView<Showing> showingsTable;

    @FXML
    private TableColumn<Showing, String> colTitle;

    @FXML
    private TableColumn<Showing, LocalDateTime> colStartDateTime;

    @FXML
    private TableColumn<Showing, LocalDateTime> colEndDateTime;

    @FXML
    private TableColumn<Showing, Integer> colSeatsLeft;

    @FXML
    private TableColumn<Showing, Integer> colAvailableSeats;

    @FXML
    private Button sellTicketsButton;

    @FXML
    private Label statusMessage;

    private Database database; // field for the shared database

    // Add setDatabase method to receive the database from MainController
    public void setDatabase(Database database) {
        this.database = database;
        // Populate the showings table with data from the database
        showingsTable.setItems(FXCollections.observableArrayList(database.getShowings()));
        System.out.println("Database instance in SalesController: " + database);
    }

    public void initialize() {
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        // LocalDateTime columns with formatting
        colStartDateTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartDateTime()));
        colEndDateTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDateTime()));

        colAvailableSeats.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAvailableSeats()).asObject());

        colStartDateTime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        });

        colEndDateTime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        });

        // Enable the "Sell Tickets" button when a showing is selected
        showingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            sellTicketsButton.setDisable(newSelection == null);
        });
    }
    public void refreshShowingsTable() {
        if (database != null) {
            showingsTable.setItems(FXCollections.observableArrayList(database.getShowings()));
            showingsTable.refresh();  // Force refresh
            System.out.println("Sell Tickets tab updated.");
        }
    }


    @FXML
    private void onSellTickets() {
        Showing selectedShowing = showingsTable.getSelectionModel().getSelectedItem();
        if (selectedShowing == null) {
            statusMessage.setText("Please select a showing.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/select-seat.fxml"));
            Parent root = loader.load();

            SeatSelectionController controller = loader.getController();
            controller.setShowing(selectedShowing, database);  // Pass database to seat selection

            Stage stage = new Stage();
            stage.setTitle("Select Seat");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            showingsTable.refresh();  // Refresh table after selling
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
