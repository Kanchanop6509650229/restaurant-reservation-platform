package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
import com.restaurant.common.events.restaurant.RestaurantEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.reservation.service.TableStatusCacheService;

@Component
public class RestaurantEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventConsumer.class);
    private final TableStatusCacheService tableStatusCacheService;

    public RestaurantEventConsumer(TableStatusCacheService tableStatusCacheService) {
        this.tableStatusCacheService = tableStatusCacheService;
    }

    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    public void consumeRestaurantEvents(RestaurantEvent event) {
        logger.info("Received restaurant event: {}", event.getClass().getSimpleName());

        if (event instanceof TableStatusChangedEvent) {
            handleTableStatusChangedEvent((TableStatusChangedEvent) event);
        } else if (event instanceof OperatingHoursChangedEvent) {
            handleOperatingHoursChangedEvent((OperatingHoursChangedEvent) event);
        } else if (event instanceof RestaurantUpdatedEvent) {
            handleRestaurantUpdatedEvent((RestaurantUpdatedEvent) event);
        }
    }
    
    @KafkaListener(
            topics = KafkaTopics.TABLE_STATUS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantKafkaListenerContainerFactory"
    )
    public void consumeTableStatusEvents(RestaurantEvent event) {
        if (event instanceof TableStatusChangedEvent) {
            handleTableStatusChangedEvent((TableStatusChangedEvent) event);
        }
    }

    private void handleTableStatusChangedEvent(TableStatusChangedEvent event) {
        logger.info("Table status changed for restaurant {}, table {}: {} -> {}", 
                event.getRestaurantId(), 
                event.getTableId(),
                event.getOldStatus(),
                event.getNewStatus());
        
        // Update table status in the cache
        tableStatusCacheService.updateTableStatus(event.getTableId(), event.getNewStatus());
    }

    private void handleOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        logger.info("Operating hours changed for restaurant {}, day {}", 
                event.getRestaurantId(), 
                event.getDayOfWeek());
        
        // Potential future implementation: Cache restaurant operating hours
    }

    private void handleRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        logger.info("Restaurant updated: {}, field: {}", 
                event.getRestaurantId(), 
                event.getFieldUpdated());
        
        // Potential future implementation: Update cached restaurant details
    }
}