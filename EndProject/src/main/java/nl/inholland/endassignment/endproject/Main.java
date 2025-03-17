package nl.inholland.endassignment.endproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.inholland.endassignment.endproject.controllers.LoginController;
import nl.inholland.endassignment.endproject.utils.Database;

public class Main extends Application {

    private final Database database = new Database();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/endassignment/endproject/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Pass the shared database to the LoginController
        LoginController loginController = fxmlLoader.getController();
        loginController.setDatabase(database);

        primaryStage.setTitle("Movie Theater App");
        primaryStage.setScene(scene);

        // Save the database on application close
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Saving data before exit...");
            database.save();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
