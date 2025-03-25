package com.restaurant.common.exceptions;


public class AuthenticationException extends BaseException {
    
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR");
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_ERROR", cause);
    }
    
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password");
    }
    
    public static AuthenticationException accountLocked() {
        return new AuthenticationException("Account is locked");
    }
    
    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("Account is disabled");
    }
    
    public static AuthenticationException accessDenied() {
        return new AuthenticationException("Access denied");
    }
}