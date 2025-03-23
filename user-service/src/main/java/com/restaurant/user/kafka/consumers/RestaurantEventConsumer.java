package com.restaurant.user.kafka.consumers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventConsumer.class);

    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(RestaurantEvent event) {
        logger.info("Received restaurant event: {}", event.getClass().getSimpleName());

        if (event instanceof RestaurantUpdatedEvent) {
            handleRestaurantUpdatedEvent((RestaurantUpdatedEvent) event);
        }
        // Add more event type handlers as needed
    }

    private void handleRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        logger.info("Restaurant updated: {} - {} from {} to {}", 
                event.getRestaurantId(), 
                event.getFieldUpdated(), 
                event.getOldValue(), 
                event.getNewValue());
        
        // Process the restaurant update
        // This could trigger notifications to users who have bookings at this restaurant
        // or update local cached data, etc.
    }
}