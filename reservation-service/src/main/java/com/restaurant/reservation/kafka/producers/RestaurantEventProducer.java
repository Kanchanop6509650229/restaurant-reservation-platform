package com.restaurant.reservation.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent;

/**
 * Kafka producer for restaurant-related events in the reservation service.
 * This class is responsible for publishing events related to restaurant validation
 * and reservation time validation to Kafka topics.
 * 
 * The producer handles two main types of events:
 * 1. Restaurant validation requests - to verify restaurant existence
 * 2. Reservation time validation requests - to verify reservation times against restaurant hours
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantEventProducer {

    /** Kafka template for sending events to Kafka topics */
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    /**
     * Constructs a new RestaurantEventProducer with the specified Kafka template.
     *
     * @param kafkaTemplate The Kafka template used for sending events
     */
    public RestaurantEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a request to validate a restaurant's existence.
     * This event is sent to the restaurant validation request topic and is used
     * to verify if a restaurant exists before processing a reservation.
     *
     * @param event The restaurant validation request event containing restaurant details
     */
    public void publishRestaurantValidationRequest(RestaurantValidationRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_VALIDATION_REQUEST, event.getCorrelationId(), event);
    }

    /**
     * Publishes a request to validate a reservation time based on restaurant hours.
     * This event is sent to the reservation time validation topic and is used
     * to verify if a requested reservation time falls within the restaurant's
     * operating hours.
     *
     * @param event The reservation time validation request event containing time details
     */
    public void publishReservationTimeValidationRequest(ReservationTimeValidationRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_TIME_VALIDATION_REQUEST, event.getCorrelationId(), event);
    }
}