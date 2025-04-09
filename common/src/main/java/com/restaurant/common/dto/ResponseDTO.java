package com.restaurant.common.dto;

import java.time.LocalDateTime;

/**
 * Generic response DTO for API responses.
 * This class provides a standardized format for all API responses,
 * including success/failure status, data, messages, and error information.
 *
 * @param <T> The type of data contained in the response
 * @author Restaurant Team
 * @version 1.0
 */
public class ResponseDTO<T> {
    /** Indicates whether the operation was successful */
    private boolean success;
    
    /** The response data of type T */
    private T data;
    
    /** A message describing the result of the operation */
    private String message;
    
    /** Error code if the operation failed */
    private String errorCode;
    
    /** Timestamp when the response was created */
    private LocalDateTime timestamp;
    
    /** Unique identifier for the request */
    private String requestId;
    
    /** Additional details about the response or error */
    private Object details;
    
    /**
     * Default constructor.
     * Initializes the timestamp to the current time.
     */
    public ResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Creates a successful response with data.
     *
     * @param <T> The type of data
     * @param data The response data
     * @return A successful ResponseDTO with the specified data
     */
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = true;
        response.data = data;
        return response;
    }
    
    /**
     * Creates a successful response with data and message.
     *
     * @param <T> The type of data
     * @param data The response data
     * @param message The success message
     * @return A successful ResponseDTO with the specified data and message
     */
    public static <T> ResponseDTO<T> success(T data, String message) {
        ResponseDTO<T> response = success(data);
        response.message = message;
        return response;
    }
    
    /**
     * Creates an error response with a message.
     *
     * @param <T> The type of data
     * @param message The error message
     * @return An error ResponseDTO with the specified message
     */
    public static <T> ResponseDTO<T> error(String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = false;
        response.message = message;
        return response;
    }
    
    /**
     * Creates an error response with a message and error code.
     *
     * @param <T> The type of data
     * @param message The error message
     * @param errorCode The error code
     * @return An error ResponseDTO with the specified message and error code
     */
    public static <T> ResponseDTO<T> error(String message, String errorCode) {
        ResponseDTO<T> response = error(message);
        response.errorCode = errorCode;
        return response;
    }
    
    /**
     * Creates an error response with a message, error code, and details.
     *
     * @param <T> The type of data
     * @param message The error message
     * @param errorCode The error code
     * @param details Additional error details
     * @return An error ResponseDTO with the specified message, error code, and details
     */
    public static <T> ResponseDTO<T> error(String message, String errorCode, Object details) {
        ResponseDTO<T> response = error(message, errorCode);
        response.details = details;
        return response;
    }
    
    /**
     * Gets the success status of the response.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the response.
     *
     * @param success The success status to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the response data.
     *
     * @return The response data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the response data.
     *
     * @param data The data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Gets the response message.
     *
     * @return The response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message The message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the error code.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode The error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the response timestamp.
     *
     * @return The response timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the response timestamp.
     *
     * @param timestamp The timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the request ID.
     *
     * @return The request ID
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Sets the request ID.
     *
     * @param requestId The request ID to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * Gets the response details.
     *
     * @return The response details
     */
    public Object getDetails() {
        return details;
    }
    
    /**
     * Sets the response details.
     *
     * @param details The details to set
     */
    public void setDetails(Object details) {
        this.details = details;
    }
}