package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static ims.Main.*;
import static ims.Validation.validateDouble;
import static ims.Validation.validatePositiveInteger;

/**
 * Controller for the add/modify product screens.
 *
 * @author Zachary Mollenhour
 */
public class EditProductController implements Initializable {
    private static final String newIdText = "Automatically Generated";
    private Stage stage;
    private int productIndex = -1;
    private ObservableList<Part> associatedParts;
    @FXML
    private Label labelViewTitle, partTablePlaceholder;
    @FXML
    private TextField inputId, inputName, inputStock, inputPrice, inputMax, inputMin;
    @FXML
    private TextField partSearch;
    @FXML
    private TableView<Part> partTableView, associatedPartTableView;
    @FXML
    private TableColumn<Part, Integer> partIdCol, partInventoryCol, associatedPartIdCol, associatedPartInventoryCol;
    @FXML
    private TableColumn<Part, String> partNameCol, associatedPartNameCol;
    @FXML
    private TableColumn<Part, Double> partPriceCol, associatedPartPriceCol;

    /**
     * Perform updates on the view to that of an Add Product form.
     */
    public void startAdd() {
        labelViewTitle.setText("Add Product");
        inputId.setText(newIdText);
    }

    /**
     * Updates the view to that of an Edit Product form.
     * This populates fields with information from an existing product.
     *
     * @param selectedIndex   the index of the Product to edit.
     * @param selectedProduct the Product to edit.
     */
    public void startEdit(int selectedIndex, Product selectedProduct) {
        labelViewTitle.setText("Modify Product");
        inputId.setText(String.valueOf(selectedProduct.getId()));
        inputName.setText(selectedProduct.getName());
        inputStock.setText(String.valueOf(selectedProduct.getStock()));
        inputPrice.setText(String.valueOf(selectedProduct.getPrice()));
        inputMax.setText(String.valueOf(selectedProduct.getMax()));
        inputMin.setText(String.valueOf(selectedProduct.getMin()));
        associatedParts.setAll(selectedProduct.getAllAssociatedParts());
        associatedPartTableView.setItems(associatedParts);
        productIndex = selectedIndex;
    }

    /**
     * Member function to search for designated key
     * Populates the key or displays a error if no key is found
     * @param keyEvent
     */
    public void onKeySearchPart(KeyEvent keyEvent) {
        String searchInput = partSearch.getText();
        partTableView.setItems(Inventory.getFilteredParts(searchInput));
        if (searchInput.isEmpty()) {
            partTablePlaceholder.setText("No parts found. To add one, go back to the main screen.");
        } else {
            partTablePlaceholder.setText("No items matched your search query. Please try again.");
            if (partTableView.getItems().size() > 0) {
                partTableView.getSelectionModel().select(0);
            }
        }
    }

    /**
     * Member Function used ot associate parts to products
     * Displays alert if the associated part is not selected
     * @param actionEvent
     */
    public void onActionAssociatePart(ActionEvent actionEvent) {
        if (partTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a part in order to add one.");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }
        associatedParts.add(partTableView.getSelectionModel().getSelectedItem());
        associatedPartTableView.setItems(associatedParts);
    }

    /** Action Event for dissociating parts from products
     * takes selected part to dissaccoaite or displays alert if none are selected
     * @param actionEvent
     */
    public void onActionDissociatePart(ActionEvent actionEvent) {
        if (associatedPartTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a part in order to remove one.");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this part?");
            fixAlertDisplay(alert);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }


            associatedParts.remove(associatedPartTableView.getSelectionModel().getSelectedItem());
            associatedPartTableView.setItems(associatedParts);
        }
    }

    /**
     * Action event for saving a part
     * Parses and validates form data for a partID and other part elements
     * Creates staged product and saves it unless an error occurs in which it displays an alert
     *
     * Within this function, I encountered a troublesome logical error in line 197
     * I had initially written if product index is greater than 0 instead of greater than or equal to 0 which would
     * cause issues to occur on the first part being modified as the inital value of the product index is -1
     * so when it added one to that the logic was invalid as it was not greater than 0
     *
     * @param actionEvent
     * @throws IOException
     */
    public void onActionSavePart(ActionEvent actionEvent) throws IOException {
        ArrayList<String> validationErrors = new ArrayList<>();
        Part stagedPart;

        // Parse and validate the form data, storing validation errors in an array.
        int id = inputId.getText().equals(newIdText) ? Inventory.getNextProductId() : Integer.parseInt(inputId.getText());
        String name = inputName.getText();
        int stock = validatePositiveInteger(inputStock, validationErrors, "Inv");
        double price = validateDouble(inputPrice, validationErrors, "Price");
        int max = validatePositiveInteger(inputMax, validationErrors, "Max");
        int min = validatePositiveInteger(inputMin, validationErrors, "Min");
        if (min > max) {
            validationErrors.add("Min must be less than Max.");
        }
        if (stock < min || stock > max) {
            validationErrors.add("Inv must be greater than Min and less than Max.");
        }

        // Initialize the staged product and associate the parts from the controller.
        Product stagedProduct = new Product(id, name, price, stock, min, max);
        for (Part stagedAssociatedPart : associatedParts) {
            stagedProduct.addAssociatedPart(stagedAssociatedPart);
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
        StringBuilder confirmData = new StringBuilder(String.format("ID: %d\nName: %s\nInventory: %d\n" +
                "Unit Price: %.2f\nMax: %d\nMin: %d\nAssociated Parts:\n", id, name, stock, price, max, min));
        for (Part stagedAssociatedPart : associatedParts) {
            confirmData.append(String.format("\t%s\n", stagedAssociatedPart.getName()));
        }
        Alert alert = detailedAlert(Alert.AlertType.CONFIRMATION,
                "Please review the following details and confirm they are correct.", confirmData.toString());
        fixAlertDisplay(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // Check if a productIndex was specified for an existing product we're editing, and update that particular
        // product in the inventory, otherwise create a new one, and then return to the main screen.
        if (productIndex >= 0) {
            Inventory.updateProduct(productIndex, stagedProduct);
        } else {
            Inventory.addProduct(stagedProduct);
        }

        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        loadView(stage, "/view/MainScreen.fxml");
    }

    /**
     * Cancels changes to return to Main Screen.
     * This checks with the user whether or not they want to discard their changes and go back.
     *
     * @param actionEvent A user input event.
     * @throws IOException
     */
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

    /**
     * Initializes the EditProduct controller.
     * Initializes the TableViews with initial contents and TableColumn/model associations.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        partTableView.setItems(Inventory.getAllParts());
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        associatedPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedPartInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        associatedParts = FXCollections.observableArrayList();
    }
}
