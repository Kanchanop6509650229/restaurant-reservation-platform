package com.restaurant.reservation.kafka.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.BaseEvent;
import com.restaurant.common.events.reservation.FindAvailableTableRequestEvent;
import com.restaurant.common.events.reservation.ReservationCancelledEvent;
import com.restaurant.common.events.reservation.ReservationConfirmedEvent;
import com.restaurant.common.events.reservation.ReservationCreatedEvent;
import com.restaurant.common.events.reservation.ReservationModifiedEvent;
import com.restaurant.common.events.reservation.TableAssignedEvent;
import com.restaurant.common.events.reservation.TableStatusEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;

/**
 * Kafka producer for reservation-related events in the reservation service.
 * This class is responsible for publishing various types of reservation events
 * to Kafka topics, including:
 * - Reservation lifecycle events (created, confirmed, cancelled, modified)
 * - Table assignment and status events
 * - Table availability requests
 * 
 * Each event is published to a specific topic with appropriate message keys
 * for efficient partitioning and message routing.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class ReservationEventProducer {

    /** Kafka template for sending events to Kafka topics */
    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    /**
     * Constructs a new ReservationEventProducer with the specified Kafka template.
     *
     * @param kafkaTemplate The Kafka template used for sending events
     */
    public ReservationEventProducer(KafkaTemplate<String, BaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a reservation created event when a new reservation is made.
     * This event is sent to the reservation creation topic and includes
     * all details of the newly created reservation.
     *
     * @param event The reservation created event containing reservation details
     */
    public void publishReservationCreatedEvent(ReservationCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_CREATE, event.getReservationId(), event);
    }

    /**
     * Publishes a reservation confirmed event when a reservation is confirmed.
     * This event is sent to the general reservation events topic and indicates
     * that a reservation has been successfully confirmed.
     *
     * @param event The reservation confirmed event containing confirmation details
     */
    public void publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_EVENTS, event.getReservationId(), event);
    }

    /**
     * Publishes a reservation cancelled event when a reservation is cancelled.
     * This event is sent to the reservation cancellation topic and includes
     * details about the cancelled reservation.
     *
     * @param event The reservation cancelled event containing cancellation details
     */
    public void publishReservationCancelledEvent(ReservationCancelledEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_CANCEL, event.getReservationId(), event);
    }

    /**
     * Publishes a reservation modified event when a reservation is updated.
     * This event is sent to the reservation update topic and includes
     * the changes made to the reservation.
     *
     * @param event The reservation modified event containing update details
     */
    public void publishReservationModifiedEvent(ReservationModifiedEvent event) {
        kafkaTemplate.send(KafkaTopics.RESERVATION_UPDATE, event.getReservationId(), event);
    }

    /**
     * Publishes a table assigned event when a table is assigned to a reservation.
     * This event is sent to the table status topic and indicates which table
     * has been assigned to which reservation.
     *
     * @param event The table assigned event containing assignment details
     */
    public void publishTableAssignedEvent(TableAssignedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getReservationId(), event);
    }
    
    /**
     * Publishes a table status changed event when a table's status changes.
     * This event is sent to the table status topic and includes details about
     * the table's new status.
     *
     * @param event The table status changed event containing status details
     */
    public void publishTableStatusChangedEvent(TableStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getTableId(), event);
    }
    
    /**
     * Publishes a table status event for general table status updates.
     * This event is sent to the table status topic and can be used for
     * various table status-related notifications.
     *
     * @param event The table status event containing status information
     */
    public void publishTableStatusEvent(TableStatusEvent event) {
        kafkaTemplate.send(KafkaTopics.TABLE_STATUS, event.getTableId(), event);
    }
    
    /**
     * Publishes a request to find an available table for a reservation.
     * This event is sent to the find available table request topic and
     * includes criteria for finding a suitable table.
     *
     * @param event The find available table request event containing search criteria
     */
    public void publishFindAvailableTableRequest(FindAvailableTableRequestEvent event) {
        kafkaTemplate.send(KafkaTopics.FIND_AVAILABLE_TABLE_REQUEST, event.getCorrelationId(), event);
    }
}