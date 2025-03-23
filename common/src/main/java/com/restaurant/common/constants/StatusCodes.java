package com.restaurant.common.constants;

public class StatusCodes {
    // General Status Codes
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String PENDING = "PENDING";
    
    // User Status Codes
    public static final String USER_ACTIVE = "ACTIVE";
    public static final String USER_INACTIVE = "INACTIVE";
    public static final String USER_LOCKED = "LOCKED";
    public static final String USER_PENDING_VERIFICATION = "PENDING_VERIFICATION";
    
    // Restaurant Status Codes
    public static final String RESTAURANT_ACTIVE = "ACTIVE";
    public static final String RESTAURANT_INACTIVE = "INACTIVE";
    public static final String RESTAURANT_MAINTENANCE = "MAINTENANCE";
    
    // Table Status Codes
    public static final String TABLE_AVAILABLE = "AVAILABLE";
    public static final String TABLE_OCCUPIED = "OCCUPIED";
    public static final String TABLE_RESERVED = "RESERVED";
    public static final String TABLE_MAINTENANCE = "MAINTENANCE";
    
    // Reservation Status Codes
    public static final String RESERVATION_PENDING = "PENDING";
    public static final String RESERVATION_CONFIRMED = "CONFIRMED";
    public static final String RESERVATION_CANCELLED = "CANCELLED";
    public static final String RESERVATION_COMPLETED = "COMPLETED";
    public static final String RESERVATION_NO_SHOW = "NO_SHOW";
    
    // Payment Status Codes
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_FAILED = "FAILED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";
    
    // Private constructor to prevent instantiation
    private StatusCodes() {}
}