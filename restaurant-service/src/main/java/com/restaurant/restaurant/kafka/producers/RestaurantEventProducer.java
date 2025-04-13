package com.restaurant.restaurant.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.common.events.restaurant.CapacityChangedEvent;
import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantOwnershipResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;

/**
 * Kafka producer for publishing restaurant-related events.
 * This producer handles:
 * - Restaurant updates and validation events
 * - Capacity and operating hours changes
 * - Table status updates and availability
 * - Reservation time validation responses
 *
 * Events are published to specific Kafka topics for asynchronous processing
 * by other services in the system.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantEventProducer {

    /** Template for publishing events to Kafka topics */
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    /**
     * Constructs a new RestaurantEventProducer with required dependencies.
     *
     * @param kafkaTemplate Template for publishing events to Kafka
     */
    public RestaurantEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes an event when a restaurant is updated.
     * Used to notify other services about changes to restaurant details.
     *
     * @param event The restaurant updated event containing changes
     */
    public void publishRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_UPDATE, event.getRestaurantId(), event);
    }

    /**
     * Publishes an event when a restaurant's capacity changes.
     * Used to notify services about changes in seating capacity.
     *
     * @param restaurantId The ID of the restaurant
     * @param oldCapacity Previous total capacity
     * @param newCapacity Updated total capacity
     * @param reason Description of why the capacity changed
     */
    public void publishCapacityChangedEvent(String restaurantId, int oldCapacity, int newCapacity, String reason) {
        CapacityChangedEvent event = new CapacityChangedEvent(restaurantId, oldCapacity, newCapacity, reason);
        kafkaTemplate.send(KafkaTopics.CAPACITY_CHANGE, restaurantId, event);
    }

    /**
     * Publishes an event when a table's status changes.
     * Used to notify services about table availability changes.
     *
     * @param event The table status changed event
     */
    public void publishTableStatusChangedEvent(TableStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getRestaurantId(), event);
    }

    /**
     * Publishes an event when operating hours are modified.
     * Used to notify services about changes in restaurant schedules.
     *
     * @param event The operating hours changed event
     */
    public void publishOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_EVENTS, event.getRestaurantId(), event);
    }

    /**
     * Publishes a response event for table availability searches.
     * Used to respond to table search requests from other services.
     *
     * @param event The find available table response event
     */
    public void publishFindAvailableTableResponse(FindAvailableTableResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.FIND_AVAILABLE_TABLE_RESPONSE, event.getCorrelationId(), event);
    }

    /**
     * Publishes a response event for restaurant validation requests.
     * Used to confirm restaurant existence and status.
     *
     * @param event The restaurant validation response event
     */
    public void publishRestaurantValidationResponse(RestaurantValidationResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_VALIDATION_RESPONSE, event.getCorrelationId(), event);
    }

    /**
     * Publishes a response event for reservation time validation.
     * Used to confirm if a requested reservation time is valid.
     *
     * @param event The reservation time validation response event
     */
    public void publishReservationTimeValidationResponse(ReservationTimeValidationResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_TIME_VALIDATION_RESPONSE, event.getCorrelationId(), event);
    }

    /**
     * Publishes a response event for restaurant ownership validation.
     * Used to confirm if a user is the owner of a restaurant.
     *
     * @param event The restaurant ownership response event
     */
    public void publishRestaurantOwnershipResponse(RestaurantOwnershipResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_OWNERSHIP_RESPONSE, event.getCorrelationId(), event);
    }
}