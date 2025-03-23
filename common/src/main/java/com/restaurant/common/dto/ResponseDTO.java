package com.restaurant.common.dto;


public class ResponseDTO<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    
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
}