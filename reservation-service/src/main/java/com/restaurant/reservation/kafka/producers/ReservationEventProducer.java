package com.restaurant.reservation.kafka.producers;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.reservation.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventProducer {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    public ReservationEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReservationCreatedEvent(ReservationCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_CREATE, event.getReservationId(), event);
    }

    public void publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_EVENTS, event.getReservationId(), event);
    }

    public void publishReservationCancelledEvent(ReservationCancelledEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_CANCEL, event.getReservationId(), event);
    }

    public void publishReservationModifiedEvent(ReservationModifiedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_UPDATE, event.getReservationId(), event);
    }

    public void publishTableAssignedEvent(TableAssignedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getReservationId(), event);
    }
}