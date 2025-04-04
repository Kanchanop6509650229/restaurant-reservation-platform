package com.restaurant.reservation.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.reservation.ReservationCancelledEvent;
import com.restaurant.common.events.reservation.ReservationConfirmedEvent;
import com.restaurant.common.events.reservation.ReservationCreatedEvent;
import com.restaurant.common.events.reservation.ReservationModifiedEvent;
import com.restaurant.common.events.reservation.TableAssignedEvent;
import com.restaurant.common.events.reservation.TableStatusEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;

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
    
    public void publishTableStatusChangedEvent(TableStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getTableId(), event);
    }
    
    public void publishTableStatusEvent(TableStatusEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getTableId(), event);
    }
}