package com.restaurant.common.exceptions;

/**
 * Base exception class for all custom exceptions in the restaurant reservation system.
 * This class extends RuntimeException and adds an error code field to provide
 * more detailed information about the type of error that occurred.
 * All custom exceptions in the system should extend this class.
 */
public class BaseException extends RuntimeException {
    /**
     * A code that identifies the specific type of error that occurred.
     * This code can be used for error handling, logging, and client-side error mapping.
     */
    private final String errorCode;
    
    /**
     * Constructs a new BaseException with the specified detail message.
     * Uses the default error code "GENERAL_ERROR".
     *
     * @param message The detail message describing the error
     */
    public BaseException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }
    
    /**
     * Constructs a new BaseException with the specified detail message and error code.
     *
     * @param message   The detail message describing the error
     * @param errorCode The specific error code identifying the type of error
     */
    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new BaseException with the specified detail message and cause.
     * Uses the default error code "GENERAL_ERROR".
     *
     * @param message The detail message describing the error
     * @param cause   The cause of the exception
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERAL_ERROR";
    }
    
    /**
     * Constructs a new BaseException with the specified detail message, error code, and cause.
     *
     * @param message   The detail message describing the error
     * @param errorCode The specific error code identifying the type of error
     * @param cause     The cause of the exception
     */
    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the error code associated with this exception.
     *
     * @return The error code as a String
     */
    public String getErrorCode() {
        return errorCode;
    }
}