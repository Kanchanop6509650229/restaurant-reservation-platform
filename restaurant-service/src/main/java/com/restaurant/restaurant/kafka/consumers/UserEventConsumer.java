package com.restaurant.restaurant.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.user.UserEvent;
import com.restaurant.common.events.user.UserLoggedInEvent;
import com.restaurant.common.events.user.UserRegisteredEvent;

/**
 * Kafka consumer for processing user-related events.
 * This consumer handles:
 * - User registration events
 * - User login events
 * - User profile updates
 * - User activity tracking
 * 
 * Events are consumed from the user events topic and processed
 * to maintain user-related data in the restaurant service.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class UserEventConsumer {

    /** Logger for user event processing */
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    /**
     * Consumes and processes user events from Kafka.
     * This method handles different types of user events by:
     * - Identifying the event type
     * - Routing to appropriate handler method
     * - Logging event processing status
     *
     * @param event The user event to process
     */
    @KafkaListener(
            topics = KafkaTopics.USER_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consume(UserEvent event) {
        logger.info("Received user event: {}", event.getClass().getSimpleName());

        if (event instanceof UserRegisteredEvent) {
            handleUserRegisteredEvent((UserRegisteredEvent) event);
        } else if (event instanceof UserLoggedInEvent) {
            handleUserLoggedInEvent((UserLoggedInEvent) event);
        }
        // Add more event handlers as needed
    }

    /**
     * Handles user registration events.
     * Processes new user registrations for:
     * - Restaurant owner account creation
     * - Analytics tracking
     * - User profile initialization
     *
     * @param event The user registration event
     */
    private void handleUserRegisteredEvent(UserRegisteredEvent event) {
        logger.info("User registered: {} ({})", event.getUsername(), event.getUserId());
        
        // Process the user registration event
        // This could be used to track restaurant owner registrations
        // or add to analytics, etc.
    }

    /**
     * Handles user login events.
     * Processes user login activities for:
     * - Activity tracking
     * - Dashboard updates
     * - Session management
     *
     * @param event The user login event
     */
    private void handleUserLoggedInEvent(UserLoggedInEvent event) {
        logger.info("User logged in: {} ({})", event.getUsername(), event.getUserId());
        
        // Process the user login event
        // This could be used to track user activity, update dashboards, etc.
    }
}