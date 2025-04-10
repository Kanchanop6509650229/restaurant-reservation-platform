package com.restaurant.reservation.kafka.consumers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.user.UserEvent;
import com.restaurant.common.events.user.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for user-related events in the reservation service.
 * This class handles various types of user events, such as user registration,
 * profile updates, and other user-related notifications.
 * 
 * The consumer can be extended to handle additional user events as needed,
 * providing a centralized location for processing user-related changes
 * that may affect the reservation system.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class UserEventConsumer {

    /** Logger instance for tracking user events */
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    /**
     * Consumes user events from the Kafka topic.
     * This method processes various types of user events and routes them
     * to appropriate handler methods based on the event type.
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
        }
        // Add more event handlers as needed
    }

    /**
     * Handles user registered events.
     * This method processes new user registrations and can be extended to
     * perform additional actions such as:
     * - Creating an initial user profile
     * - Sending welcome emails
     * - Setting up default preferences
     * - Initializing user-specific data
     *
     * @param event The user registered event containing user details
     */
    private void handleUserRegisteredEvent(UserRegisteredEvent event) {
        logger.info("User registered: {} ({})", event.getUsername(), event.getUserId());
        
        // Process the user registration event
        // This could be used to create an initial profile, welcome email, etc.
    }
}