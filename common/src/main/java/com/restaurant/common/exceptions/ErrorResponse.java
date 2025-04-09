package com.restaurant.common.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A standardized response object for error handling in the REST API.
 * This class provides a consistent structure for error responses, including
 * timestamp, error message, error code, request path, and any validation errors.
 */
public class ErrorResponse {
    /**
     * The date and time when the error occurred.
     */
    private final LocalDateTime timestamp;
    
    /**
     * A human-readable message describing the error.
     */
    private final String message;
    
    /**
     * A code that identifies the specific type of error that occurred.
     * This code can be used for error handling and client-side error mapping.
     */
    private final String errorCode;
    
    /**
     * The path of the request that caused the error.
     */
    private final String path;
    
    /**
     * A map of field names to their corresponding validation error messages.
     * This is used for validation errors and may be null for other types of errors.
     */
    private final Map<String, String> errors;
    
    /**
     * Constructs a new ErrorResponse with basic error information.
     *
     * @param message   A human-readable message describing the error
     * @param errorCode A code identifying the type of error
     * @param path      The path of the request that caused the error
     */
    public ErrorResponse(String message, String errorCode, String path) {
        this(message, errorCode, path, null);
    }
    
    /**
     * Constructs a new ErrorResponse with complete error information.
     *
     * @param message   A human-readable message describing the error
     * @param errorCode A code identifying the type of error
     * @param path      The path of the request that caused the error
     * @param errors    A map of field names to their validation error messages
     */
    public ErrorResponse(String message, String errorCode, String path, Map<String, String> errors) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.errors = errors;
    }
    
    /**
     * Gets the date and time when the error occurred.
     *
     * @return The timestamp as a LocalDateTime
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the human-readable error message.
     *
     * @return The error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the error code identifying the type of error.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the path of the request that caused the error.
     *
     * @return The request path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Gets the map of validation errors, if any.
     *
     * @return A map of field names to their validation error messages, or null if no validation errors
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}