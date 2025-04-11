package com.restaurant.reservation.kafka.producers;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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

    /** Logger for this producer */
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventProducer.class);

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
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishReservationCreatedEvent(ReservationCreatedEvent event) {
        if (event == null || event.getReservationId() == null) {
            logger.error("Cannot publish null reservation created event or event with null reservation ID");
            return false;
        }

        try {
            logger.info("Publishing reservation created event: reservationId={}, restaurantId={}",
                    event.getReservationId(), event.getRestaurantId());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESERVATION_CREATE)
                    .setHeader(KafkaHeaders.KEY, event.getReservationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Reservation created event sent successfully: reservationId={}, offset={}",
                            event.getReservationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send reservation created event: reservationId={}, error={}",
                            event.getReservationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing reservation created event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a reservation confirmed event when a reservation is confirmed.
     * This event is sent to the general reservation events topic and indicates
     * that a reservation has been successfully confirmed.
     *
     * @param event The reservation confirmed event containing confirmation details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        if (event == null || event.getReservationId() == null) {
            logger.error("Cannot publish null reservation confirmed event or event with null reservation ID");
            return false;
        }

        try {
            logger.info("Publishing reservation confirmed event: reservationId={}", event.getReservationId());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESERVATION_EVENTS)
                    .setHeader(KafkaHeaders.KEY, event.getReservationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Reservation confirmed event sent successfully: reservationId={}, offset={}",
                            event.getReservationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send reservation confirmed event: reservationId={}, error={}",
                            event.getReservationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing reservation confirmed event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a reservation cancelled event when a reservation is cancelled.
     * This event is sent to the reservation cancellation topic and includes
     * details about the cancelled reservation.
     *
     * @param event The reservation cancelled event containing cancellation details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishReservationCancelledEvent(ReservationCancelledEvent event) {
        if (event == null || event.getReservationId() == null) {
            logger.error("Cannot publish null reservation cancelled event or event with null reservation ID");
            return false;
        }

        try {
            logger.info("Publishing reservation cancelled event: reservationId={}, reason={}",
                    event.getReservationId(), event.getReason());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESERVATION_CANCEL)
                    .setHeader(KafkaHeaders.KEY, event.getReservationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Reservation cancelled event sent successfully: reservationId={}, offset={}",
                            event.getReservationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send reservation cancelled event: reservationId={}, error={}",
                            event.getReservationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing reservation cancelled event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a reservation modified event when a reservation is updated.
     * This event is sent to the reservation update topic and includes
     * the changes made to the reservation.
     *
     * @param event The reservation modified event containing update details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishReservationModifiedEvent(ReservationModifiedEvent event) {
        if (event == null || event.getReservationId() == null) {
            logger.error("Cannot publish null reservation modified event or event with null reservation ID");
            return false;
        }

        try {
            logger.info("Publishing reservation modified event: reservationId={}, from {} to {} people",
                    event.getReservationId(), event.getOldPartySize(), event.getNewPartySize());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESERVATION_UPDATE)
                    .setHeader(KafkaHeaders.KEY, event.getReservationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Reservation modified event sent successfully: reservationId={}, offset={}",
                            event.getReservationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send reservation modified event: reservationId={}, error={}",
                            event.getReservationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing reservation modified event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a table assigned event when a table is assigned to a reservation.
     * This event is sent to the table status topic and indicates which table
     * has been assigned to which reservation.
     *
     * @param event The table assigned event containing assignment details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishTableAssignedEvent(TableAssignedEvent event) {
        if (event == null || event.getReservationId() == null || event.getTableId() == null) {
            logger.error("Cannot publish null table assigned event or event with null IDs");
            return false;
        }

        try {
            logger.info("Publishing table assigned event: reservationId={}, tableId={}",
                    event.getReservationId(), event.getTableId());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.TABLE_STATUS)
                    .setHeader(KafkaHeaders.KEY, event.getReservationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Table assigned event sent successfully: reservationId={}, tableId={}, offset={}",
                            event.getReservationId(), event.getTableId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send table assigned event: reservationId={}, tableId={}, error={}",
                            event.getReservationId(), event.getTableId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing table assigned event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a table status changed event when a table's status changes.
     * This event is sent to the table status topic and includes details about
     * the table's new status.
     *
     * @param event The table status changed event containing status details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishTableStatusChangedEvent(TableStatusChangedEvent event) {
        if (event == null || event.getTableId() == null) {
            logger.error("Cannot publish null table status changed event or event with null table ID");
            return false;
        }

        try {
            logger.info("Publishing table status changed event: tableId={}, oldStatus={}, newStatus={}",
                    event.getTableId(), event.getOldStatus(), event.getNewStatus());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.TABLE_STATUS)
                    .setHeader(KafkaHeaders.KEY, event.getTableId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Table status changed event sent successfully: tableId={}, offset={}",
                            event.getTableId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send table status changed event: tableId={}, error={}",
                            event.getTableId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing table status changed event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a table status event for general table status updates.
     * This event is sent to the table status topic and can be used for
     * various table status-related notifications.
     *
     * @param event The table status event containing status information
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishTableStatusEvent(TableStatusEvent event) {
        if (event == null || event.getTableId() == null) {
            logger.error("Cannot publish null table status event or event with null table ID");
            return false;
        }

        try {
            logger.info("Publishing table status event: tableId={}, status={}",
                    event.getTableId(), event.getNewStatus());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.TABLE_STATUS)
                    .setHeader(KafkaHeaders.KEY, event.getTableId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Table status event sent successfully: tableId={}, offset={}",
                            event.getTableId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send table status event: tableId={}, error={}",
                            event.getTableId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing table status event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a request to find an available table for a reservation.
     * This event is sent to the find available table request topic and
     * includes criteria for finding a suitable table.
     *
     * @param event The find available table request event containing search criteria
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishFindAvailableTableRequest(FindAvailableTableRequestEvent event) {
        if (event == null || event.getCorrelationId() == null) {
            logger.error("Cannot publish null find available table request event or event with null correlation ID");
            return false;
        }

        try {
            logger.info("Publishing find available table request: correlationId={}, restaurantId={}, partySize={}",
                    event.getCorrelationId(), event.getRestaurantId(), event.getPartySize());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.FIND_AVAILABLE_TABLE_REQUEST)
                    .setHeader(KafkaHeaders.KEY, event.getCorrelationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Find available table request sent successfully: correlationId={}, offset={}",
                            event.getCorrelationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send find available table request: correlationId={}, error={}",
                            event.getCorrelationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing find available table request: {}", e.getMessage(), e);
            return false;
        }
    }
}