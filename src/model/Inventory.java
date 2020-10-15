package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Application-internal database for parts and products.
 * For this class, in a future version I would look into also including the ability
 * to associate parts and products to users, whether it be via a userid or users name
 * This would allow for tracking of who created what parts and elements
 *
 * @author Zachary Mollenhour
 */
public class Inventory {
    private static final ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private static ObservableList<Part> filteredParts = FXCollections.observableArrayList();
    private static ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private static String lastPartSearch = "";
    private static String lastProductSearch = "";

    /**
     * Inserts a new Part into the Inventory.
     * @param newPart
     */
    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    /**
     * Looks up a Part by ID.
     * @param partId
     * @return part or null
     */
    public static Part lookupPart(int partId) {
        for (Part part : allParts) {
            if (part.getId() == partId) {
                return part;
            }
        }

        return null;
    }

    /**
     * Filters the Parts Inventory by a user-provided string.
     * Part names are matched in a case-insensitive fashion into a new list and returned.
     * @param partName
     * @return result
     */
    public static ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> result = FXCollections.observableArrayList();
        partName = partName.toLowerCase();

        for (Part part : allParts) {
            if (part.getName().toLowerCase().contains(partName)) {
                result.add(part);
            }
        }

        return result;
    }

    /**
     * Updates a Part.
     * @param index
     * @param newPart
     *
     */
    public static void updatePart(int index, Part newPart) {
        allParts.set(index, newPart);
    }

    /**
     * Deletes a Part.
     * @param selectedPart
     */
    public static boolean deletePart(Part selectedPart) {
        return allParts.remove(selectedPart);
    }

    /**
     * @return A list of all Parts.
     */
    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     * Filters the Parts Inventory by a user-provided string.
     * Part names and IDs are matched in a case-insensitive fashion and are stored in a static variable.
     * @param searchQuery
     */
    public static ObservableList<Part> getFilteredParts(String searchQuery) {
        return getFilteredParts(searchQuery, false);
    }

    /**
     * Filters the Parts Inventory by a user-provided string.
     * Part names and IDs are matched in a case-insensitive fashion and are stored in a static variable.
     * @param reset
     * @param searchQuery
     */
    public static ObservableList<Part> getFilteredParts(String searchQuery, boolean reset) {
        // If we get a query for the same string twice in a row, no extra work needs to be done.
        if (!reset && searchQuery.equals(lastPartSearch)) {
            return filteredParts;
        }

        filteredParts.clear();


        if (searchQuery.matches("\\d+")) {
            int lookupId = Integer.parseInt(searchQuery);
            Part foundPart = Inventory.lookupPart(lookupId);
            if (foundPart != null) {
                filteredParts.add(foundPart);
            }
            ObservableList<Part> foundParts = Inventory.lookupPart(searchQuery);
            // There's a chance that lookupPart will include the Part found earlier, so ensure it's not duplicated.
            foundParts.removeIf(p -> (p.getId() == lookupId));
            filteredParts.addAll(foundParts);
        } else {
            filteredParts = Inventory.lookupPart(searchQuery);
        }

        // Store the search string associated with the current state of filteredParts
        lastPartSearch = searchQuery;
        return filteredParts;
    }

    /**
     * Inserts a new Product into the Inventory.
     * @param newProduct
     */
    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * Looks up a Product by ID.
     * @param productId
     *
     */
    public static Product lookupProduct(int productId) {
        for (Product product : allProducts) {
            if (product.getId() == productId) {
                return product;
            }
        }

        return null;
    }

    /**
     * Filters the Products Inventory by a user-provided string.
     * Product names are matched in a case-insensitive fashion into a new list and returned.
     * @param productName
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> result = FXCollections.observableArrayList();
        productName = productName.toLowerCase();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(productName)) {
                result.add(product);
            }
        }

        return result;
    }

    /**
     * Updates a Product.
     * @param newProduct
     * @param index
     */
    public static void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    /**
     * Deletes a Product.
     * @param selectedProduct
     */
    public static boolean deleteProduct(Product selectedProduct) {
        return allProducts.remove(selectedProduct);
    }

    /**
     * @return A list of all Products.
     */
    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }

    /**
     * Filters the Products Inventory by a user-provided string.
     * Part names and IDs are matched in a case-insensitive fashion and are stored in a static variable.
     * @param searchQuery
     */
    public static ObservableList<Product> getFilteredProducts(String searchQuery) {
        return getFilteredProducts(searchQuery, false);
    }

    /**
     * Filters the Products Inventory by a user-provided string.
     * Part names and IDs are matched in a case-insensitive fashion and are stored in a static variable.
     * @param searchQuery
     * @param reset
     */
    public static ObservableList<Product> getFilteredProducts(String searchQuery, boolean reset) {

        if (!reset && searchQuery.equals(lastProductSearch)) {
            return filteredProducts;
        }

        filteredProducts.clear();
        // If the search query is a positive integer, we want to lookup a Product whose ID matches.
        // However, it's possible that the product name also has digits, so we want to return those as well.
        if (searchQuery.matches("\\d+")) {
            int lookupId = Integer.parseInt(searchQuery);
            Product foundProduct = Inventory.lookupProduct(lookupId);
            if (foundProduct != null) {
                filteredProducts.add(foundProduct);
            }
            ObservableList<Product> foundProducts = Inventory.lookupProduct(searchQuery);

            foundProducts.removeIf(p -> (p.getId() == lookupId));
            filteredProducts.addAll(foundProducts);
        } else {
            filteredProducts = Inventory.lookupProduct(searchQuery);
        }


        lastProductSearch = searchQuery;
        return filteredProducts;
    }

    /**
     * Identifies the next available Part ID.
     * @return max+1
     */
    public static int getNextPartId() {
        int max = 0;
        for (Part part : allParts) {
            int id = part.getId();
            if (id > max) {
                max = id;
            }
        }
        return max + 1;
    }

    /**
     * Identifies the next available Product ID
     * @return max+1
     */
    public static int getNextProductId() {
        int max = 0;
        for (Product product : allProducts) {
            int id = product.getId();
            if (id > max) {
                max = id;
            }
        }
        return max + 1;
    }
}
