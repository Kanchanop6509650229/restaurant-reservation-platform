package com.restaurant.reservation.kafka.consumers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.user.UserEvent;
import com.restaurant.common.events.user.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

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

    private void handleUserRegisteredEvent(UserRegisteredEvent event) {
        logger.info("User registered: {} ({})", event.getUsername(), event.getUserId());
        
        // Process the user registration event
        // This could be used to create an initial profile, welcome email, etc.
    }
}