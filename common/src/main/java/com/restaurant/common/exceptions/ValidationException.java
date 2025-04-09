package com.restaurant.common.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when input validation fails.
 * This exception is used to collect and report multiple validation errors
 * that may occur during the validation of user input or business rules.
 * Extends BaseException with error code "VALIDATION_ERROR".
 */
public class ValidationException extends BaseException {
    /**
     * A map of field names to their corresponding validation error messages.
     * This allows collecting multiple validation errors in a single exception.
     */
    private final Map<String, String> validationErrors;
    
    /**
     * Constructs a new ValidationException with a general error message.
     * Initializes an empty map for validation errors.
     *
     * @param message The general validation error message
     */
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = new HashMap<>();
    }
    
    /**
     * Constructs a new ValidationException with a general error message and a map of validation errors.
     *
     * @param message           The general validation error message
     * @param validationErrors A map of field names to their validation error messages
     */
    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = validationErrors;
    }
    
    /**
     * Constructs a new ValidationException for a single field validation error.
     *
     * @param field        The name of the field that failed validation
     * @param errorMessage The validation error message for the field
     */
    public ValidationException(String field, String errorMessage) {
        super("Validation error for field: " + field, "VALIDATION_ERROR");
        this.validationErrors = new HashMap<>();
        this.validationErrors.put(field, errorMessage);
    }
    
    /**
     * Gets the map of validation errors.
     *
     * @return A map of field names to their validation error messages
     */
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
    
    /**
     * Adds a new validation error for a specific field.
     *
     * @param field        The name of the field that failed validation
     * @param errorMessage The validation error message for the field
     */
    public void addValidationError(String field, String errorMessage) {
        this.validationErrors.put(field, errorMessage);
    }
}