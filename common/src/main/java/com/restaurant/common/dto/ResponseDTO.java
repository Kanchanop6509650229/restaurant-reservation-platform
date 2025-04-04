package com.restaurant.common.dto;

import java.time.LocalDateTime;

public class ResponseDTO<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String requestId;
    private Object details;
    
    public ResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Static factory methods
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = true;
        response.data = data;
        return response;
    }
    
    public static <T> ResponseDTO<T> success(T data, String message) {
        ResponseDTO<T> response = success(data);
        response.message = message;
        return response;
    }
    
    public static <T> ResponseDTO<T> error(String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = false;
        response.message = message;
        return response;
    }
    
    public static <T> ResponseDTO<T> error(String message, String errorCode) {
        ResponseDTO<T> response = error(message);
        response.errorCode = errorCode;
        return response;
    }
    
    public static <T> ResponseDTO<T> error(String message, String errorCode, Object details) {
        ResponseDTO<T> response = error(message, errorCode);
        response.details = details;
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Object getDetails() {
        return details;
    }
    
    public void setDetails(Object details) {
        this.details = details;
    }
}