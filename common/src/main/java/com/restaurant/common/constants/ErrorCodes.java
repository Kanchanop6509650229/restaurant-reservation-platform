package com.restaurant.common.constants;

public class ErrorCodes {
    // General Error Codes
    public static final String GENERAL_ERROR = "GENERAL_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    
    // User Service Error Codes
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    
    // Restaurant Service Error Codes
    public static final String RESTAURANT_NOT_FOUND = "RESTAURANT_NOT_FOUND";
    public static final String TABLE_NOT_FOUND = "TABLE_NOT_FOUND";
    public static final String TABLE_NOT_AVAILABLE = "TABLE_NOT_AVAILABLE";
    public static final String INVALID_OPERATING_HOURS = "INVALID_OPERATING_HOURS";
    
    // Reservation Service Error Codes
    public static final String RESERVATION_NOT_FOUND = "RESERVATION_NOT_FOUND";
    public static final String RESERVATION_CONFLICT = "RESERVATION_CONFLICT";
    public static final String RESTAURANT_FULLY_BOOKED = "RESTAURANT_FULLY_BOOKED";
    public static final String RESERVATION_EXPIRED = "RESERVATION_EXPIRED";
    public static final String INVALID_RESERVATION_TIME = "INVALID_RESERVATION_TIME";
    public static final String RESTAURANT_NOT_ACTIVE = "RESTAURANT_NOT_ACTIVE";
    public static final String OUTSIDE_OPERATING_HOURS = "OUTSIDE_OPERATING_HOURS";
    
    // Payment Service Error Codes
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";
    
    // Private constructor to prevent instantiation
    private ErrorCodes() {}
}