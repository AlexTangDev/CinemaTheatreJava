package nl.inholland.endassignment.endproject.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.FileChooser;
import nl.inholland.endassignment.endproject.models.Sale;
import nl.inholland.endassignment.endproject.utils.Database;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SalesHistoryController {
    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, String> customerColumn;
    @FXML private TableColumn<Sale, String> showingColumn;
    @FXML private TableColumn<Sale, String> startTimeColumn;
    @FXML private TableColumn<Sale, String> endTimeColumn;
    @FXML private TableColumn<Sale, String> seatsColumn;
    @FXML private Button exportButton;

    private Database database;  // Reference to the shared database

    public void setDatabase(Database database) {
        this.database = database;
        System.out.println("Database instance in SalesHistoryController: " + database);
        refreshTable();  // Load sales history when the database is set
    }

    @FXML
    public void initialize() {
        // Set cell value factories for each column
        customerColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomerName()));
        showingColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getShowing().getTitle())); // Fetch title from Showing
        startTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getShowing().getStartDateTime().toString())); // Fetch start time
        endTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getShowing().getEndDateTime().toString())); // Fetch end time
        seatsColumn.setCellValueFactory(cellData -> {
            // Convert seat numbers list to a comma-separated string
            List<Integer> seatNumbers = cellData.getValue().getSeatNumbers();
            return new SimpleStringProperty(seatNumbers.toString().replaceAll("[\\[\\]]", ""));
        });

        // Bind column widths to percentages of the table width
        customerColumn.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.15)); // 15%
        showingColumn.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.25)); // 25%
        startTimeColumn.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.20)); // 20%
        endTimeColumn.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.20)); // 20%
        seatsColumn.prefWidthProperty().bind(salesTable.widthProperty().multiply(0.20)); // 20%
    }

    public void refreshTable() {
        if (database != null) {
            salesTable.setItems(FXCollections.observableArrayList(database.getSalesHistory()));
            salesTable.refresh();
            System.out.println("Sales history updated in UI.");
        }
    }

    @FXML
    private void onRefresh() {
        System.out.println("Manual refresh triggered.");
        refreshTable();
    }

    @FXML
    private void onExportCustomers() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Customer Export");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (file != null) {
            exportCustomersToCSV(file);
        }
    }

    private void exportCustomersToCSV(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("name,email,last_sale");

            // Get unique customers with email
            database.getSalesHistory().stream()
                    .filter(sale -> sale.getCustomerEmail() != null) // Only include customers with email
                    .collect(Collectors.groupingBy(Sale::getCustomerEmail)) // Group by email
                    .forEach((email, sales) -> {
                        String name = sales.get(0).getCustomerName(); // Assume the name doesn't change
                        LocalDateTime lastSale = sales.stream()
                                .map(Sale::getSaleDate) // Use sale date instead of showing start date
                                .max(LocalDateTime::compareTo) // Find the most recent sale
                                .orElse(null);

                        if (lastSale != null) {
                            writer.println(name + "," + email + "," + lastSale.toLocalDate());
                        }
                    });

            System.out.println("Customer data successfully exported to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to export customers: " + e.getMessage());
        }
    }

}
