package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A model for products
 * @author Zachary Mollenhour
 */
public class Product {
    private final ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock, min, max;

    /**
     * A model for products
     * @param id
     * @param name
     * @param price
     * @param stock
     * @param min
     * @param max
     */
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;

        this.associatedParts = FXCollections.observableArrayList();
    }

    /**
     * @return the unique identifier of the Product
     */
    public int getId() {
        return id;
    }

    /**
     * @param id an unique identifier to set for the Product
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name of the Product
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the Product to set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the unit price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the unit price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the amount of inventory available
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the amount of inventory available to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return the minimum
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the minimum to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the maximum
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the maximum to set
     */
    public void setMax(int max) {
        this.max = max;
    }


    /**
     * Adds associated part
     * @param part
     */
    public void addAssociatedPart(Part part) {
        associatedParts.add(part);
    }

    /**
     * Deletes an associated Part reference.
     * @param selectedAssociatedPart
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        return associatedParts.remove(selectedAssociatedPart);
    }

    /**
     * Gets a list of associated Parts.
     * @return associatedParts
     */
    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }
}
