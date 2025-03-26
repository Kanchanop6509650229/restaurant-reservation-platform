package com.restaurant.user.dto;

public class LoginResponse {

    private String token;
    private String message;
    private String userId;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
    
    public LoginResponse(String token, String message, String userId) {
        this.token = token;
        this.message = message;
        this.userId = userId;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}