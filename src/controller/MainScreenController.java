package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static ims.Main.fixAlertDisplay;
import static ims.Main.loadView;

/**
 * Controller for the main screen.
 *
 * @author Zachary Mollenhour
 */
public class MainScreenController implements Initializable {
    Stage stage;
    @FXML
    private Label partTablePlaceholder, productTablePlaceholder;
    @FXML
    private TextField partSearch, productSearch;
    @FXML
    private TableView<Part> partTableView;
    @FXML
    private TableColumn<Part, Integer> partIdCol, partInventoryCol;
    @FXML
    private TableColumn<Part, String> partNameCol;
    @FXML
    private TableColumn<Part, Double> partPriceCol;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> productIdCol, productInventoryCol;
    @FXML
    private TableColumn<Product, String> productNameCol;
    @FXML
    private TableColumn<Product, Double> productPriceCol;


    /**
     *
     */
    private void refreshPartTable() {
        refreshPartTable(false);
    }


    /**
     * Function used for refreshing the parts table in the main screen when
     * performing a serach
     * Displays an error if no items are found
     * @param reset
     */
    private void refreshPartTable(boolean reset) {
        String searchInput = partSearch.getText();
        partTableView.setItems(Inventory.getFilteredParts(searchInput, reset));
        if (searchInput.isEmpty()) {
            partTablePlaceholder.setText("Click Add to add a new part.");
        } else {
            partTablePlaceholder.setText("No items matched your search query. Please try again.");
            if (partTableView.getItems().size() > 0) {
                partTableView.getSelectionModel().select(0);
            }
        }
    }


    @FXML
    public void onKeySearchPart(KeyEvent keyEvent) {
        refreshPartTable();
    }


    /**
     * Action event for adding parts and calling the Editpart view
     * @param actionEvent for clicking add part button
     * @throws IOException error to be thrown
     */
    @FXML
    public void onActionAddPart(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = loadView(stage, "/view/EditPartView.fxml");
        EditPartController editCtrl = loader.getController();
        editCtrl.startAdd();
    }


    /**
     * Action event for modifying  a part
     * Displays an error if the form input is empty
     * calls the edit part view if succesful
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void onActionModifyPart(ActionEvent actionEvent) throws IOException {
        if (partTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a part to modify!");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }

        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = loadView(stage, "/view/EditPartView.fxml");
        EditPartController editCtrl = loader.getController();
        editCtrl.startEdit(partTableView.getSelectionModel().getSelectedIndex(), partTableView.getSelectionModel().getSelectedItem());
    }


    /**
     * Action event for deletion of a part
     * If successful will display a confirmation form
     * Otherwise, it will display an alert
     * @param actionEvent
     */
    @FXML
    public void onActionDeletePart(ActionEvent actionEvent) {
        if (partTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a part to delete!");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }

        Part selectedPart = partTableView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the following part?\n\n" + selectedPart.getName());
        fixAlertDisplay(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Inventory.deletePart(selectedPart);
            refreshPartTable(true);
        }
    }


    private void refreshProductTable() {
        refreshProductTable(false);
    }


    /**
     * Reset function for refreshing the product table adn searching parts on main screen
     * @param reset
     */
    private void refreshProductTable(boolean reset) {
        String searchInput = productSearch.getText();
        productTableView.setItems(Inventory.getFilteredProducts(searchInput, reset));
        if (searchInput.isEmpty()) {
            productTablePlaceholder.setText("Click Add to add a new product.");
        } else {
            productTablePlaceholder.setText("No items matched your search query. Please try again.");
            if (productTableView.getItems().size() > 0) {
                productTableView.getSelectionModel().select(0);
            }
        }
    }


    @FXML
    public void onKeySearchProduct(KeyEvent event) {
        refreshProductTable();
    }


    /**
     * Action Event for adding a product
     * If succesful will call the edit product view
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void onActionAddProduct(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = loadView(stage, "/view/EditProductView.fxml");
        EditProductController editCtrl = loader.getController();
        editCtrl.startAdd();
    }


    /**
     * Action event for modifying a product. If no input is found it will display an alert
     * Otherwise, it calls the edit product view
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void onActionModifyProduct(ActionEvent actionEvent) throws IOException {
        if (productTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a product to modify!");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }

        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = loadView(stage, "/view/EditProductView.fxml");
        EditProductController editCtrl = loader.getController();
        editCtrl.startEdit(productTableView.getSelectionModel().getSelectedIndex(), productTableView.getSelectionModel().getSelectedItem());
    }


    /**
     * Action event for deleting a product
     * If no input is found it will display an alert, if there are parts associated it will display error message
     * Otherwise, it displays a confirmation alert
     * @param actionEvent
     */
    @FXML
    public void onActionDeleteProduct(ActionEvent actionEvent) {
        if (productTableView.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must select a product to delete!");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }
        if (!productTableView.getSelectionModel().getSelectedItem().getAllAssociatedParts().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "The selected product has associated parts.\n" +
                    "Disassociate the parts before deleting.");
            fixAlertDisplay(alert);
            alert.showAndWait();
            return;
        }

        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the following product?\n\n" + selectedProduct.getName());
        fixAlertDisplay(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Inventory.deleteProduct(selectedProduct);
            refreshProductTable(true);
        }
    }

    @FXML
    public void onActionExit(ActionEvent actionEvent) {
        System.exit(0);
    }


    /**
     * initalizer constuctor
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        partTableView.setItems(Inventory.getAllParts());
        productTableView.setItems(Inventory.getAllProducts());
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
