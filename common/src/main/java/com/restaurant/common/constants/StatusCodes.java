package com.restaurant.common.constants;

/**
 * Class containing all status codes used across the system.
 * This class provides a centralized location for all status codes
 * to ensure consistency in status tracking across microservices.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class StatusCodes {
    // General Status Codes
    /** Indicates successful operation */
    public static final String SUCCESS = "SUCCESS";
    
    /** Indicates operation failed */
    public static final String ERROR = "ERROR";
    
    /** Indicates operation is in progress */
    public static final String PENDING = "PENDING";
    
    // User Status Codes
    /** Indicates user account is active and can be used */
    public static final String USER_ACTIVE = "ACTIVE";
    
    /** Indicates user account is inactive and cannot be used */
    public static final String USER_INACTIVE = "INACTIVE";
    
    /** Indicates user account is locked due to security reasons */
    public static final String USER_LOCKED = "LOCKED";
    
    /** Indicates user account is pending email verification */
    public static final String USER_PENDING_VERIFICATION = "PENDING_VERIFICATION";
    
    // Restaurant Status Codes
    /** Indicates restaurant is open and accepting reservations */
    public static final String RESTAURANT_ACTIVE = "ACTIVE";
    
    /** Indicates restaurant is closed and not accepting reservations */
    public static final String RESTAURANT_INACTIVE = "INACTIVE";
    
    /** Indicates restaurant is under maintenance */
    public static final String RESTAURANT_MAINTENANCE = "MAINTENANCE";
    
    // Table Status Codes
    /** Indicates table is available for reservation */
    public static final String TABLE_AVAILABLE = "AVAILABLE";
    
    /** Indicates table is currently occupied by customers */
    public static final String TABLE_OCCUPIED = "OCCUPIED";
    
    /** Indicates table is reserved for future use */
    public static final String TABLE_RESERVED = "RESERVED";
    
    /** Indicates table is under maintenance and not available */
    public static final String TABLE_MAINTENANCE = "MAINTENANCE";
    
    // Reservation Status Codes
    /** Indicates reservation is pending confirmation */
    public static final String RESERVATION_PENDING = "PENDING";
    
    /** Indicates reservation has been confirmed */
    public static final String RESERVATION_CONFIRMED = "CONFIRMED";
    
    /** Indicates reservation has been cancelled */
    public static final String RESERVATION_CANCELLED = "CANCELLED";
    
    /** Indicates reservation has been completed */
    public static final String RESERVATION_COMPLETED = "COMPLETED";
    
    /** Indicates customer did not show up for the reservation */
    public static final String RESERVATION_NO_SHOW = "NO_SHOW";
    
    // Payment Status Codes
    /** Indicates payment is pending processing */
    public static final String PAYMENT_PENDING = "PENDING";
    
    /** Indicates payment has been successfully processed */
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    
    /** Indicates payment processing failed */
    public static final String PAYMENT_FAILED = "FAILED";
    
    /** Indicates payment has been refunded */
    public static final String PAYMENT_REFUNDED = "REFUNDED";
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private StatusCodes() {}
}