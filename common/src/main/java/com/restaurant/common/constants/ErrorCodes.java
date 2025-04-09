package com.restaurant.common.constants;

/**
 * Class containing all error codes used across the system.
 * This class provides a centralized location for all error codes
 * to ensure consistency in error handling across microservices.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class ErrorCodes {
    // General Error Codes
    /** General system error that doesn't fit into other categories */
    public static final String GENERAL_ERROR = "GENERAL_ERROR";
    
    /** Error indicating invalid input data */
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    
    /** Error indicating requested resource was not found */
    public static final String NOT_FOUND = "NOT_FOUND";
    
    /** Error indicating user is not authenticated */
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    
    /** Error indicating user is not authorized to perform the action */
    public static final String FORBIDDEN = "FORBIDDEN";
    
    // User Service Error Codes
    /** Error indicating user account was not found */
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    
    /** Error indicating user account already exists */
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    
    /** Error indicating invalid username or password */
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    
    /** Error indicating user account is locked */
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    
    // Restaurant Service Error Codes
    /** Error indicating restaurant was not found */
    public static final String RESTAURANT_NOT_FOUND = "RESTAURANT_NOT_FOUND";
    
    /** Error indicating table was not found */
    public static final String TABLE_NOT_FOUND = "TABLE_NOT_FOUND";
    
    /** Error indicating table is not available for reservation */
    public static final String TABLE_NOT_AVAILABLE = "TABLE_NOT_AVAILABLE";
    
    /** Error indicating invalid operating hours configuration */
    public static final String INVALID_OPERATING_HOURS = "INVALID_OPERATING_HOURS";
    
    // Reservation Service Error Codes
    /** Error indicating reservation was not found */
    public static final String RESERVATION_NOT_FOUND = "RESERVATION_NOT_FOUND";
    
    /** Error indicating reservation time conflicts with existing reservation */
    public static final String RESERVATION_CONFLICT = "RESERVATION_CONFLICT";
    
    /** Error indicating restaurant is fully booked for requested time */
    public static final String RESTAURANT_FULLY_BOOKED = "RESTAURANT_FULLY_BOOKED";
    
    /** Error indicating reservation has expired */
    public static final String RESERVATION_EXPIRED = "RESERVATION_EXPIRED";
    
    /** Error indicating invalid reservation time */
    public static final String INVALID_RESERVATION_TIME = "INVALID_RESERVATION_TIME";
    
    /** Error indicating restaurant is not currently active */
    public static final String RESTAURANT_NOT_ACTIVE = "RESTAURANT_NOT_ACTIVE";
    
    /** Error indicating reservation time is outside operating hours */
    public static final String OUTSIDE_OPERATING_HOURS = "OUTSIDE_OPERATING_HOURS";
    
    // Payment Service Error Codes
    /** Error indicating payment processing failed */
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    
    /** Error indicating invalid payment method */
    public static final String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ErrorCodes() {}
}