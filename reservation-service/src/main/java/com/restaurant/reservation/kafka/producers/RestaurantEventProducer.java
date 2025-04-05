package com.restaurant.reservation.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent;

/**
 * Kafka producer for restaurant-related events.
 */
@Component
public class RestaurantEventProducer {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    public RestaurantEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a request to validate a restaurant's existence.
     */
    public void publishRestaurantValidationRequest(RestaurantValidationRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_VALIDATION_REQUEST, event.getCorrelationId(), event);
    }

    /**
     * Publishes a request to validate a reservation time based on restaurant hours.
     */
    public void publishReservationTimeValidationRequest(ReservationTimeValidationRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_TIME_VALIDATION_REQUEST, event.getCorrelationId(), event);
    }
}