package com.restaurant.restaurant.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.common.events.restaurant.CapacityChangedEvent;
import com.restaurant.common.events.restaurant.OperatingHoursChangedEvent;
import com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;

@Component
public class RestaurantEventProducer {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    public RestaurantEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_UPDATE, event.getRestaurantId(), event);
    }

    public void publishCapacityChangedEvent(String restaurantId, int oldCapacity, int newCapacity, String reason) {
        CapacityChangedEvent event = new CapacityChangedEvent(restaurantId, oldCapacity, newCapacity, reason);
        kafkaTemplate.send(KafkaTopics.CAPACITY_CHANGE, restaurantId, event);
    }

    public void publishTableStatusChangedEvent(TableStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getRestaurantId(), event);
    }

    public void publishOperatingHoursChangedEvent(OperatingHoursChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_EVENTS, event.getRestaurantId(), event);
    }

    public void publishFindAvailableTableResponse(FindAvailableTableResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.FIND_AVAILABLE_TABLE_RESPONSE, event.getCorrelationId(), event);
    }

    public void publishRestaurantValidationResponse(RestaurantValidationResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.RESTAURANT_VALIDATION_RESPONSE, event.getCorrelationId(), event);
    }

    public void publishReservationTimeValidationResponse(ReservationTimeValidationResponseEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_TIME_VALIDATION_RESPONSE, event.getCorrelationId(), event);
    }
}