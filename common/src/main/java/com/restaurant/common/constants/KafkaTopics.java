package com.restaurant.common.constants;

public class KafkaTopics {
    // User Service Topics
    public static final String USER_EVENTS = "user-events";
    public static final String USER_REGISTRATION = "user-registration";
    public static final String USER_LOGIN = "user-login";
    public static final String USER_PROFILE = "user-profile";
    
    // Restaurant Service Topics
    public static final String RESTAURANT_EVENTS = "restaurant-events";
    public static final String RESTAURANT_UPDATE = "restaurant-update";
    public static final String TABLE_STATUS = "table-status";
    public static final String CAPACITY_CHANGE = "capacity-change";
    
    // Reservation Service Topics
    public static final String RESERVATION_EVENTS = "reservation-events";
    public static final String RESERVATION_CREATE = "reservation-create";
    public static final String RESERVATION_UPDATE = "reservation-update";
    public static final String RESERVATION_CANCEL = "reservation-cancel";
    
    // Table Availability Topics
    public static final String FIND_AVAILABLE_TABLE_REQUEST = "find-available-table-request";
    public static final String FIND_AVAILABLE_TABLE_RESPONSE = "find-available-table-response";

    public static final String RESTAURANT_VALIDATION_REQUEST = "restaurant-validation-request";
    public static final String RESTAURANT_VALIDATION_RESPONSE = "restaurant-validation-response";
    public static final String RESERVATION_TIME_VALIDATION_REQUEST = "reservation-time-validation-request";
    public static final String RESERVATION_TIME_VALIDATION_RESPONSE = "reservation-time-validation-response";
    
    // Notification Service Topics
    public static final String NOTIFICATION_EVENTS = "notification-events";
    
    // Private constructor to prevent instantiation
    private KafkaTopics() {}
}