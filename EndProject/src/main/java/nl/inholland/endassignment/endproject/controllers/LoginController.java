package nl.inholland.endassignment.endproject.controllers;

import nl.inholland.endassignment.endproject.models.User;
import nl.inholland.endassignment.endproject.utils.Database;
import nl.inholland.endassignment.endproject.exceptions.AccountLockedException;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.util.Arrays;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private Database database;

    private final List<User> users = Arrays.asList(
            new User("manager1", "Pass123!", "Manager"),
            new User("sales1", "Sales456@", "Sales")
    );

    private int failedAttempts = 0; // Counter for failed login attempts
    private final int MAX_ATTEMPTS = 3; // Maximum allowed attempts

    public void setDatabase(Database database) {
        this.database = database;
    }

    @FXML
    public void login() {
        errorLabel.setText(""); // Clear any previous error messages

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            // Validate user credentials
            User user = validateCredentials(username, password);

            // If credentials are valid, proceed to main view
            openMainView(user);

        } catch (AccountLockedException e) {
            // Handle the account lock exception (show alert and exit application)
            showAccountLockedDialog(e.getMessage());
        } catch (Exception e) {
            // For invalid credentials, display an error message and increment failed attempts
            errorLabel.setText("Invalid username/password combination!");
            failedAttempts++;

            // If the user exceeds the max attempts, throw AccountLockedException
            if (failedAttempts >= MAX_ATTEMPTS) {
                try {
                    throw new AccountLockedException("Your account has been locked");
                } catch (AccountLockedException ex) {
                    showAccountLockedDialog(ex.getMessage());
                }
            }
        }
    }

    private User validateCredentials(String username, String password) throws Exception {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                failedAttempts = 0; // Reset failed attempts after successful login
                return user;
            }
        }
        throw new Exception("Invalid credentials"); // Invalid credentials case
    }

    private void openMainView(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/main.fxml"));
            Parent root = fxmlLoader.load();

            MainController mainController = fxmlLoader.getController();
            mainController.setDatabase(database); // Pass the shared database
            mainController.setUser(user); // Pass the logged-in user

            Scene scene = new Scene(root, 600, 400);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Movie Theater Main Window");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAccountLockedDialog(String message) {
        // Show an alert dialog with the account lock message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Account locked");
        alert.setContentText(message);
        alert.showAndWait();

        // Exit the application after the dialog is dismissed
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
