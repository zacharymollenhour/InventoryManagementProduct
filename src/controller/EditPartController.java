package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.InHouse;
import model.Inventory;
import model.Outsourced;
import model.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static ims.Main.*;
import static ims.Validation.validateDouble;
import static ims.Validation.validatePositiveInteger;

/**
 * Controller for the add/modify part screens.
 *
 * @author Zachary Mollenhour
 */
public class EditPartController {
    private final String newIdText = "To be calculated";
    private final String labelAltTextInHouse = "Machine ID";
    private final String labelAltTextOutsourced = "Company Name";
    private Stage stage;
    private int partIndex = -1;
    @FXML
    private Label labelViewTitle, labelAlt;
    @FXML
    private ToggleGroup selectPartSource;
    @FXML
    private RadioButton inputSourceIn, inputSourceOut;
    @FXML
    private TextField inputId, inputName, inputStock, inputPrice, inputMax, inputMin, inputAlt;

    /**
     * * Updates the view to that of an Add Part form.
     */
    public void startAdd() {
        labelViewTitle.setText("Add Part");
        labelAlt.setText(labelAltTextInHouse);
        inputSourceIn.setSelected(true);
        inputId.setText(newIdText);
    }

    /**
     * Updates the view to that of an Edit Part form.
     * @param selectedIndex
     * @param selectedPart
     */
    public void startEdit(int selectedIndex, Part selectedPart) {
        labelViewTitle.setText("Modify Part");
        if (selectedPart instanceof InHouse) {
            labelAlt.setText(labelAltTextInHouse);
            inputSourceIn.setSelected(true);
            inputAlt.setText(String.valueOf(((InHouse) selectedPart).getMachineId()));
        } else if (selectedPart instanceof Outsourced) {
            labelAlt.setText(labelAltTextOutsourced);
            inputSourceOut.setSelected(true);
            inputAlt.setText(String.valueOf(((Outsourced) selectedPart).getCompanyName()));
        }
        inputId.setText(String.valueOf(selectedPart.getId()));
        inputName.setText(selectedPart.getName());
        inputStock.setText(String.valueOf(selectedPart.getStock()));
        inputPrice.setText(String.valueOf(selectedPart.getPrice()));
        inputMax.setText(String.valueOf(selectedPart.getMax()));
        inputMin.setText(String.valueOf(selectedPart.getMin()));
        partIndex = selectedIndex;
    }


    /**
     * Updates UI to reflect a Part's sourcing.
     * @param actionEvent
     */
    @FXML
    public void onActionChangeSource(ActionEvent actionEvent) {
        if (inputSourceIn.isSelected()) {
            labelAlt.setText(labelAltTextInHouse);
        } else if (inputSourceOut.isSelected()) {
            labelAlt.setText(labelAltTextOutsourced);
        }
    }


    /**
     * Saves changes and returns to the Main Screen.
     * This confirms changes with the user about the Part they're adding or modifying and then saves those changes.
     *
     * Within this function, I encountered a troublesome logical error in line 197
     * I had initially written if partIndex greater than  0 instead of greater than or equal than 0 which would
     * cause issues to occur on the first part being modified as the initial value of the partIndex is -1
     * so when it added one to that the logic was invalid as it was not greater than 0
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void onActionSavePart(ActionEvent actionEvent) throws IOException {
        ArrayList<String> validationErrors = new ArrayList<>();
        Part stagedPart;

        // Parse and validate the form data, storing validation errors in an array.
        int id = inputId.getText().equals(newIdText) ? Inventory.getNextPartId() : Integer.parseInt(inputId.getText());
        String name = inputName.getText();
        int stock = validatePositiveInteger(inputStock, validationErrors, "Inv");
        double price = validateDouble(inputPrice, validationErrors, "Price/Cost");
        int max = validatePositiveInteger(inputMax, validationErrors, "Max");
        int min = validatePositiveInteger(inputMin, validationErrors, "Min");
        if (min > max) {
            validationErrors.add("Min must be less than Max.");
        }
        if (stock < min || stock > max) {
            validationErrors.add("Inv must be greater than Min and less than Max.");
        }
        // And depending on the selection, initialize our staged part appropriately.
        if (inputSourceIn.isSelected()) {
            int machineId = validatePositiveInteger(inputAlt, validationErrors, labelAltTextInHouse);
            stagedPart = new InHouse(id, name, price, stock, min, max, machineId);
        } else {
            String companyName = inputAlt.getText();
            stagedPart = new Outsourced(id, name, price, stock, min, max, companyName);
        }

        // Inform the user if any validation failed, and return to the edit screen if so.
        if (validationErrors.size() > 0) {
            Alert alert = detailedAlert(Alert.AlertType.ERROR, "One or more fields failed to validate. Please " +
                    "review the following and make the necessary corrections.", String.join("\n", validationErrors));
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }

        // Prepare and display a confirmation dialog showing (mostly) parsed values.
        String confirmData = String.format("ID: %d\nName: %s\nInventory: %d\nUnit Price: %.2f\nMax: %d\nMin: %d\n%s: %s",
                id, name, stock, price, max, min, labelAlt.getText(), inputAlt.getText());
        Alert alert = detailedAlert(Alert.AlertType.CONFIRMATION,
                "Please review the following details and confirm they are correct.", confirmData);
        fixAlertDisplay(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // Check if a partIndex was specified for an existing part we're editing, and update that particular part in the
        // inventory, otherwise create a new one, and then return to the main screen.
        if (partIndex >= 0) {
            Inventory.updatePart(partIndex, stagedPart);
        } else {
            Inventory.addPart(stagedPart);
        }

        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        loadView(stage, "/view/MainScreen.fxml");
    }


    /**
     * Cancels changes to return to Main Screen.
     * This checks with the user whether or not they want to discard their changes and go back.
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void onActionCancel(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your changes will be discarded. Is this OK?");
        fixAlertDisplay(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        loadView(stage, "/view/MainScreen.fxml");
    }
}
