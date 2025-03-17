package nl.inholland.endassignment.endproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import nl.inholland.endassignment.endproject.models.User;
import nl.inholland.endassignment.endproject.utils.Database;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label timeLabel;
    @FXML private TabPane tabPane;

    private Database database;

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setUser(User user) {
        welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
        roleLabel.setText("You are logged in as " + user.getRole() + ".");
        timeLabel.setText("The current date and time is " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        loadTabsForRole(user);
    }

    private void loadTabsForRole(User user) {
        switch (user.getRole()) {
            case "Manager" -> {
                loadSalesTab();
                loadShowingsTab();
                loadSalesHistoryTab();
            }
            case "Sales" -> loadSalesTab();
            default -> throw new IllegalArgumentException("Unknown role: " + user.getRole());
        }
    }

    private void loadSalesTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/sales.fxml"));
            Parent salesContent = loader.load();
            SalesController controller = loader.getController();
            controller.setDatabase(database);

            Tab tab = new Tab("Sell Tickets", salesContent);
            tab.setClosable(false);
            tab.setOnSelectionChanged(event -> {
                if (tab.isSelected()) controller.refreshShowingsTable();
            });

            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadShowingsTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/showings.fxml"));
            Parent content = loader.load();
            ShowingsController controller = loader.getController();
            controller.setDatabase(database); // Pass the shared database to ShowingsController

            Tab tab = new Tab("Manage Showings", content);
            tab.setClosable(false);
            tab.setOnSelectionChanged(event -> {
                if (tab.isSelected()) {
                    System.out.println("Refreshing Manage Showings tab...");
                    controller.refreshTable(); // Refresh table when the tab is selected
                }
            });

            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadSalesHistoryTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/salesHistory.fxml"));
            Parent content = loader.load();
            SalesHistoryController controller = loader.getController();
            controller.setDatabase(database);

            Tab tab = new Tab("View Sales History", content);
            tab.setClosable(false);
            tab.setOnSelectionChanged(event -> {
                if (tab.isSelected()) controller.refreshTable();
            });

            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshSalesTab() {
        for (Tab tab : tabPane.getTabs()) {
            System.out.println("Tab: " + tab.getText());
            if (tab.getText().equals("Sell Tickets")) {
                SalesController salesController = (SalesController) tab.getContent().getUserData();
                System.out.println("SalesController reference: " + salesController);
                if (salesController != null) {
                    salesController.refreshShowingsTable();  // Refresh the "Sell Tickets" tab
                }
            }
        }
    }
}
