package ims;

import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Provides validation functions for user input.
 *
 * @author Zachary Mollenhour
 */
public class Validation {
    /**
     * Validates that a given input is a positive integer.
     * Checks that an input can be parsed as an integer and if not, appends an error to a provided array.
     * @param fieldName
     * @param input
     * @param validationErrors
     * @return 0
     */
    public static int validatePositiveInteger(TextField input, ArrayList<String> validationErrors, String fieldName) {
        if (input.getText().matches("\\d+")) {
            return Integer.parseInt(input.getText());
        }
        validationErrors.add(fieldName + " must be a positive integer.");
        return 0;
    }

    /**
     * Validates that a given input is a double.
     * Checks that an input can be parsed as a double and if not, appends an error to a provided array.
     * @param input
     * @param validationErrors
     * @param fieldName
     * @return 0
     */
    public static double validateDouble(TextField input, ArrayList<String> validationErrors, String fieldName) {
        if (input.getText().matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(input.getText());
        }
        validationErrors.add(fieldName + " must be a decimal number.");
        return 0;
    }
}
