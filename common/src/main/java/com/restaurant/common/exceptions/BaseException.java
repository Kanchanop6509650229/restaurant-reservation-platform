package com.restaurant.common.exceptions;

public class BaseException extends RuntimeException {
    private final String errorCode;
    
    public BaseException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }
    
    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERAL_ERROR";
    }
    
    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}