package com.restaurant.common.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String message;
    private final String errorCode;
    private final String path;
    private final Map<String, String> errors;
    
    public ErrorResponse(String message, String errorCode, String path) {
        this(message, errorCode, path, null);
    }
    
    public ErrorResponse(String message, String errorCode, String path, Map<String, String> errors) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.errors = errors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getPath() {
        return path;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}