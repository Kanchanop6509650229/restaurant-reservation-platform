package com.restaurant.common.constants;

/**
 * Class containing all Kafka topic names used in the system.
 * This class provides a centralized location for all Kafka topic names
 * to ensure consistency across the microservices.
 * 
 * @author Restaurant Team
 * @version 1.0
 */
public class KafkaTopics {
    // User Service Topics
    /** Topic for all user-related events */
    public static final String USER_EVENTS = "user-events";
    
    /** Topic for user registration events */
    public static final String USER_REGISTRATION = "user-registration";
    
    /** Topic for user login events */
    public static final String USER_LOGIN = "user-login";
    
    /** Topic for user profile updates */
    public static final String USER_PROFILE = "user-profile";
    
    // Restaurant Service Topics
    /** Topic for all restaurant-related events */
    public static final String RESTAURANT_EVENTS = "restaurant-events";
    
    /** Topic for restaurant information updates */
    public static final String RESTAURANT_UPDATE = "restaurant-update";
    
    /** Topic for table status changes */
    public static final String TABLE_STATUS = "table-status";
    
    /** Topic for restaurant capacity changes */
    public static final String CAPACITY_CHANGE = "capacity-change";
    
    // Reservation Service Topics
    /** Topic for all reservation-related events */
    public static final String RESERVATION_EVENTS = "reservation-events";
    
    /** Topic for new reservation creation */
    public static final String RESERVATION_CREATE = "reservation-create";
    
    /** Topic for reservation updates */
    public static final String RESERVATION_UPDATE = "reservation-update";
    
    /** Topic for reservation cancellations */
    public static final String RESERVATION_CANCEL = "reservation-cancel";
    
    // Table Availability Topics
    /** Topic for requests to find available tables */
    public static final String FIND_AVAILABLE_TABLE_REQUEST = "find-available-table-request";
    
    /** Topic for responses to find available tables requests */
    public static final String FIND_AVAILABLE_TABLE_RESPONSE = "find-available-table-response";

    /** Topic for restaurant validation requests */
    public static final String RESTAURANT_VALIDATION_REQUEST = "restaurant-validation-request";
    
    /** Topic for restaurant validation responses */
    public static final String RESTAURANT_VALIDATION_RESPONSE = "restaurant-validation-response";
    
    /** Topic for reservation time validation requests */
    public static final String RESERVATION_TIME_VALIDATION_REQUEST = "reservation-time-validation-request";
    
    /** Topic for reservation time validation responses */
    public static final String RESERVATION_TIME_VALIDATION_RESPONSE = "reservation-time-validation-response";
    
    // Notification Service Topics
    /** Topic for all notification-related events */
    public static final String NOTIFICATION_EVENTS = "notification-events";
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private KafkaTopics() {}
}