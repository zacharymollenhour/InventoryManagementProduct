package ims;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.InHouse;
import model.Inventory;
import model.Outsourced;
import model.Product;

import java.io.IOException;

/**
 * The main application source code.
 *
 * @author Zachary Mollenhour
 */
public class Main extends Application {
    /**
     * Main file with source code that provides entry into application.
     * Populates the Inventory with a couple of Parts and Products, then starts the JavaFX application.
     * @param args
     */
    public static void main(String[] args) {


        launch(args);
    }

    /**
     * Loads a new View.
     * This updates the stage to a new View with some size bounding.
     * @param stage
     * @param view
     */
    public static FXMLLoader loadView(Stage stage, String view) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(view));
        loader.load();
        Scene scene = new Scene(loader.getRoot());
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());

        return loader;
    }

    /**
     * Wrapper around Alert to fix sizing.
     * @param alert alert
     *
     */
    public static void fixAlertDisplay(Alert alert) {
        alert.setResizable(true);
        Platform.runLater(() -> alert.setResizable(false));
    }

    /**
     * Wrapper function to create a detailed Alert.
     * This prepares a label and expanded textarea in a typical alert dialog.
     * @param details to get details of alert
     * @param message message of alert
     * @param type type of alert
     * @return alert
     */
    public static Alert detailedAlert(Alert.AlertType type, String message, String details) {
        Alert alert = new Alert(type);
        Label topMessage = new Label(message);
        TextArea detailsBox = new TextArea(details);

        detailsBox.setEditable(false);
        detailsBox.setWrapText(true);
        detailsBox.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(detailsBox, Priority.ALWAYS);
        GridPane body = new GridPane();
        body.add(topMessage, 0, 0);
        body.add(detailsBox, 0, 1);
        alert.getDialogPane().setContent(body);

        return alert;
    }

    /**
     * The main entry point for this JavaFX application.
     * This is called after init and sets up the MainScreen View as our entry scene.
     * @param primaryStage for view
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        loadView(primaryStage, "/view/MainScreen.fxml");
        primaryStage.setTitle("Inventory Management System");
    }
}
