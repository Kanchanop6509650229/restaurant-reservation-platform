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
        if (event == null) {
            logger.warn("Received null restaurant event");
            return;
        }

        logger.info("Received restaurant event: {}, restaurantId={}",
                event.getClass().getSimpleName(), event.getRestaurantId());

        try {
            if (event instanceof TableStatusChangedEvent) {
                handleTableStatusChangedEvent((TableStatusChangedEvent) event);
            } else if (event instanceof OperatingHoursChangedEvent) {
                handleOperatingHoursChangedEvent((OperatingHoursChangedEvent) event);
            } else if (event instanceof RestaurantUpdatedEvent) {
                handleRestaurantUpdatedEvent((RestaurantUpdatedEvent) event);
            } else {
                logger.debug("Unhandled restaurant event type: {}", event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Error processing restaurant event: {}, type: {}",
                    e.getMessage(), event.getClass().getSimpleName(), e);
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
        if (event == null) {
            logger.warn("Received null table status event");
            return;
        }

        String eventType = event.getClass().getSimpleName();
        logger.info("Received table status event: {}", eventType);

        try {
            if (event instanceof TableStatusChangedEvent) {
                TableStatusChangedEvent statusEvent = (TableStatusChangedEvent) event;
                logger.debug("Processing table status change: tableId={}, oldStatus={}, newStatus={}",
                        statusEvent.getTableId(), statusEvent.getOldStatus(), statusEvent.getNewStatus());
                handleTableStatusChangedEvent(statusEvent);
            } else if (event instanceof TableStatusEvent) {
                TableStatusEvent statusEvent = (TableStatusEvent) event;
                logger.debug("Processing table status event: tableId={}, newStatus={}",
                        statusEvent.getTableId(), statusEvent.getNewStatus());
                handleTableStatusEvent(statusEvent);
            } else {
                logger.warn("Unhandled table status event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing table status event: {}, type: {}",
                    e.getMessage(), eventType, e);
        }
    }

    /**
     * Handles table status changed events by updating the table status cache.
     * This method logs the status change and updates the cache with the new status.
     *
     * @param event The table status changed event containing status details
     */
    /**
     * Handles table status changed events by updating the table status cache.
     * This method logs the status change and updates the cache with the new status.
     *
     * @param event The table status changed event containing status details
     */
    private void handleTableStatusChangedEvent(TableStatusChangedEvent event) {
        if (event == null || event.getTableId() == null) {
            logger.warn("Received invalid table status changed event");
            return;
        }

        logger.info("Table status changed for restaurant {}, table {}: {} -> {}",
                event.getRestaurantId(),
                event.getTableId(),
                event.getOldStatus(),
                event.getNewStatus());

        try {
            // Update table status in the cache
            tableStatusCacheService.updateTableStatus(event.getTableId(), event.getNewStatus());
            logger.debug("Successfully updated table status in cache: tableId={}, status={}",
                    event.getTableId(), event.getNewStatus());
        } catch (Exception e) {
            logger.error("Failed to update table status in cache: tableId={}, error={}",
                    event.getTableId(), e.getMessage(), e);
        }
    }

    /**
     * Handles operating hours changed events.
     * This method logs the operating hours change and can be extended to
     * update cached operating hours information.
     *
     * @param event The operating hours changed event containing schedule details
     */

    private void handleOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        if (event == null || event.getRestaurantId() == null) {
            logger.warn("Received invalid operating hours changed event");
            return;
        }

        logger.info("Operating hours changed for restaurant {}, day {}, new hours: {} - {}",
                event.getRestaurantId(),
                event.getDayOfWeek(),
                event.getNewOpenTime(),
                event.getNewCloseTime());

        // Potential future implementation: Cache restaurant operating hours
        // This could involve updating a local cache of restaurant operating hours
        // to avoid having to query the restaurant service for this information
    }

    /**
     * Handles restaurant updated events.
     * This method logs the restaurant update and can be extended to
     * update cached restaurant information.
     *
     * @param event The restaurant updated event containing update details
     */

    private void handleRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        if (event == null || event.getRestaurantId() == null) {
            logger.warn("Received invalid restaurant updated event");
            return;
        }

        logger.info("Restaurant updated: {}, field: {}, new value: {}",
                event.getRestaurantId(),
                event.getFieldUpdated(),
                event.getNewValue());

        // Potential future implementation: Update cached restaurant details
        // This could involve updating a local cache of restaurant information
        // such as name, address, capacity, etc.
    }

    /**
     * Handles table status events.
     * This method processes general table status events and updates the cache accordingly.
     *
     * @param event The table status event containing status information
     */
    private void handleTableStatusEvent(TableStatusEvent event) {
        if (event == null || event.getTableId() == null) {
            logger.warn("Received invalid table status event");
            return;
        }

        logger.info("Table status event for restaurant {}, table {}: {} -> {}, reason: {}",
                event.getRestaurantId(),
                event.getTableId(),
                event.getOldStatus(),
                event.getNewStatus(),
                event.getReason());

        try {
            // Update table status in the cache
            tableStatusCacheService.updateTableStatus(event.getTableId(), event.getNewStatus());
            logger.debug("Successfully updated table status in cache from event: tableId={}, status={}",
                    event.getTableId(), event.getNewStatus());
        } catch (Exception e) {
            logger.error("Failed to update table status in cache from event: tableId={}, error={}",
                    event.getTableId(), e.getMessage(), e);
        }
    }
}