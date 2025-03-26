package com.restaurant.reservation.kafka.consumers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
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
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    public void consume(RestaurantEvent event) {
        logger.info("Received restaurant event: {}", event.getClass().getSimpleName());

        if (event instanceof TableStatusChangedEvent) {
            handleTableStatusChangedEvent((TableStatusChangedEvent) event);
        } else if (event instanceof OperatingHoursChangedEvent) {
            handleOperatingHoursChangedEvent((OperatingHoursChangedEvent) event);
        } else if (event instanceof RestaurantUpdatedEvent) {
            handleRestaurantUpdatedEvent((RestaurantUpdatedEvent) event);
        }
    }

    private void handleTableStatusChangedEvent(TableStatusChangedEvent event) {
        logger.info("Table status changed for restaurant {}, table {}: {} -> {}", 
                event.getRestaurantId(), 
                event.getTableId(),
                event.getOldStatus(),
                event.getNewStatus());
        
        // Handle table status change
        // This might involve updating reservations or checking for conflicts
    }

    private void handleOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        logger.info("Operating hours changed for restaurant {}, day {}", 
                event.getRestaurantId(), 
                event.getDayOfWeek());
        
        // Handle operating hours change
        // This might involve checking for affected reservations
    }

    private void handleRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        logger.info("Restaurant updated: {}, field: {}", 
                event.getRestaurantId(), 
                event.getFieldUpdated());
        
        // Handle restaurant information update
        // This might involve updating cached restaurant data
    }
}