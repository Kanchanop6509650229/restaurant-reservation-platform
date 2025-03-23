package com.restaurant.common.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends BaseException {
    private final Map<String, String> validationErrors;
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(String field, String errorMessage) {
        super("Validation error for field: " + field, "VALIDATION_ERROR");
        this.validationErrors = new HashMap<>();
        this.validationErrors.put(field, errorMessage);
    }
    
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
    
    public void addValidationError(String field, String errorMessage) {
        this.validationErrors.put(field, errorMessage);
    }
}