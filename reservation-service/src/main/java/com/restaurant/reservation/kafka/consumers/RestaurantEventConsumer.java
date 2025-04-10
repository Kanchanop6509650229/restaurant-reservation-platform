package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.reservation.TableStatusEvent;
import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
import com.restaurant.common.events.restaurant.RestaurantEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.reservation.service.TableStatusCacheService;

/**
 * Kafka consumer for restaurant-related events in the reservation service.
 * This class handles various types of restaurant events, including:
 * - Table status changes
 * - Operating hours updates
 * - Restaurant information updates
 * 
 * The consumer maintains a cache of table statuses and can be extended
 * to handle additional restaurant-related events in the future.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantEventConsumer {

    /** Logger instance for tracking restaurant events */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventConsumer.class);
    
    /** Service for managing table status cache */
    private final TableStatusCacheService tableStatusCacheService;

    /**
     * Constructs a new RestaurantEventConsumer with the specified table status cache service.
     *
     * @param tableStatusCacheService The service for managing table status cache
     */
    public RestaurantEventConsumer(TableStatusCacheService tableStatusCacheService) {
        this.tableStatusCacheService = tableStatusCacheService;
    }

    /**
     * Consumes general restaurant events from the Kafka topic.
     * This method processes various types of restaurant events and routes them
     * to appropriate handler methods based on the event type.
     *
     * @param event The restaurant event to process
     */
    @KafkaListener(topics = KafkaTopics.RESTAURANT_EVENTS, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "restaurantKafkaListenerContainerFactory")
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

    /**
     * Consumes table status events from the Kafka topic.
     * This method processes table status changes and updates the local cache
     * accordingly.
     *
     * @param event The table status event to process
     */
    @KafkaListener(topics = KafkaTopics.TABLE_STATUS, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "restaurantKafkaListenerContainerFactory")
    public void consumeTableStatusEvents(Object event) {
        logger.info("Received table status event: {}", event.getClass().getSimpleName());

        try {
            if (event instanceof TableStatusChangedEvent) {
                handleTableStatusChangedEvent((TableStatusChangedEvent) event);
            } else if (event instanceof TableStatusEvent) {
                // Handle TableStatusEvent if needed
                // This might require implementing a new method
            }
        } catch (Exception e) {
            logger.error("Error processing table status event: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles table status changed events by updating the table status cache.
     * This method logs the status change and updates the cache with the new status.
     *
     * @param event The table status changed event containing status details
     */
    private void handleTableStatusChangedEvent(TableStatusChangedEvent event) {
        logger.info("Table status changed for restaurant {}, table {}: {} -> {}",
                event.getRestaurantId(),
                event.getTableId(),
                event.getOldStatus(),
                event.getNewStatus());

        // Update table status in the cache
        tableStatusCacheService.updateTableStatus(event.getTableId(), event.getNewStatus());
    }

    /**
     * Handles operating hours changed events.
     * This method logs the operating hours change and can be extended to
     * update cached operating hours information.
     *
     * @param event The operating hours changed event containing schedule details
     */
    private void handleOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        logger.info("Operating hours changed for restaurant {}, day {}",
                event.getRestaurantId(),
                event.getDayOfWeek());

        // Potential future implementation: Cache restaurant operating hours
    }

    /**
     * Handles restaurant updated events.
     * This method logs the restaurant update and can be extended to
     * update cached restaurant information.
     *
     * @param event The restaurant updated event containing update details
     */
    private void handleRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        logger.info("Restaurant updated: {}, field: {}",
                event.getRestaurantId(),
                event.getFieldUpdated());

        // Potential future implementation: Update cached restaurant details
    }
}