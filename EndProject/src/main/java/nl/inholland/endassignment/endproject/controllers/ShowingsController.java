package nl.inholland.endassignment.endproject.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.inholland.endassignment.endproject.models.Showing;
import nl.inholland.endassignment.endproject.utils.Database;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ShowingsController {

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
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private RadioButton allShowingsRadioButton;

    @FXML
    private RadioButton futureShowingsRadioButton;

    private Database database;  // Reference to the shared database
    private ObservableList<Showing> allShowings; // List to hold all showings for filtering

    // Add the setDatabase() method to receive the database from MainController
    public void setDatabase(Database database) {
        this.database = database;
        this.allShowings = FXCollections.observableArrayList(database.getShowings());
        refreshTable();  // Populate the table with the data from the shared database
    }

    public void initialize() {
        ToggleGroup filterToggleGroup = new ToggleGroup();
        allShowingsRadioButton.setToggleGroup(filterToggleGroup);
        futureShowingsRadioButton.setToggleGroup(filterToggleGroup);

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStartDateTime.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        colEndDateTime.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        colSeatsLeft.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        // Enable buttons only when a showing is selected
        showingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    // Method to filter the table based on selected radio button
    @FXML
    public void filterShowings() {
        if (futureShowingsRadioButton.isSelected()) {
            System.out.println("Future showings selected");
            // Filter only future showings
            List<Showing> futureShowings = allShowings.stream()
                    .filter(showing -> showing.getStartDateTime().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
            System.out.println("Filtered showings: " + futureShowings.size());
            showingsTable.setItems(FXCollections.observableArrayList(futureShowings));
        } else {
            System.out.println("All showings selected");
            // Show all showings
            showingsTable.setItems(allShowings);
        }
    }

    public void refreshTable() {
        allShowings.setAll(database.getShowings()); // Reload the list of all showings from the database
        filterShowings(); // Apply the current filter
        showingsTable.refresh(); // Force a visual update
    }

    @FXML
    public void onAddShowing() {
        Showing newShowing = new Showing("New Movie", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 72);
        database.addShowing(newShowing);  // Add to database
        refreshTable();  // Refresh the UI table
    }

    @FXML
    private void onEditShowing() {
        Showing selectedShowing = showingsTable.getSelectionModel().getSelectedItem();
        if (selectedShowing == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/edit-showing.fxml"));
            Parent root = loader.load();

            EditShowingController controller = loader.getController();
            controller.setShowingToEdit(selectedShowing); // Pass the selected showing to the editor

            Stage stage = new Stage();
            stage.setTitle("Edit Showing");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isSaved()) {
                refreshTable(); // Refresh the table after the edit window closes
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteShowing() {
        Showing selectedShowing = showingsTable.getSelectionModel().getSelectedItem();
        if (selectedShowing == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this showing?");
        alert.setContentText("Title: " + selectedShowing.getTitle());

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            database.removeShowing(selectedShowing);  // Remove from database
            refreshTable();  // Update the table after deleting
        }
    }

    @FXML
    public void openAddShowingWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/add-showing.fxml"));
            Parent root = loader.load();
            AddShowingController controller = loader.getController();

            // Pass database and a new callback to refresh both tables
            controller.setDatabase(database, () -> {
                refreshTable();  // Refresh "Manage Showings" table
                MainController mainController = (MainController) showingsTable.getScene().getUserData();
                System.out.println("MainController reference: " + mainController);
                if (mainController != null) {
                    mainController.refreshSalesTab();  // Also refresh "Sell Tickets" tab
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Add New Showing");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
