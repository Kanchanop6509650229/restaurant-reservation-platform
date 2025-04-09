package com.restaurant.common.exceptions;

/**
 * Exception thrown when authentication fails or when there are issues with user authentication.
 * This exception is used to handle various authentication-related errors such as invalid credentials,
 * locked accounts, disabled accounts, and access denied scenarios.
 * Extends BaseException with error code "AUTHENTICATION_ERROR".
 */
public class AuthenticationException extends BaseException {
    
    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message The detail message describing the authentication error
     */
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR");
    }
    
    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     *
     * @param message The detail message describing the authentication error
     * @param cause   The cause of the authentication error
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_ERROR", cause);
    }
    
    /**
     * Creates a new AuthenticationException for invalid credentials.
     * This is used when the username or password provided is incorrect.
     *
     * @return A new AuthenticationException with a message about invalid credentials
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password");
    }
    
    /**
     * Creates a new AuthenticationException for a locked account.
     * This is used when an account has been locked due to too many failed login attempts
     * or administrative action.
     *
     * @return A new AuthenticationException with a message about the locked account
     */
    public static AuthenticationException accountLocked() {
        return new AuthenticationException("Account is locked");
    }
    
    /**
     * Creates a new AuthenticationException for a disabled account.
     * This is used when an account has been disabled by an administrator.
     *
     * @return A new AuthenticationException with a message about the disabled account
     */
    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("Account is disabled");
    }
    
    /**
     * Creates a new AuthenticationException for access denied.
     * This is used when a user attempts to access a resource they don't have permission for.
     *
     * @return A new AuthenticationException with a message about access being denied
     */
    public static AuthenticationException accessDenied() {
        return new AuthenticationException("Access denied");
    }
}