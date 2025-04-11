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
import com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;

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

    /** Logger for this producer */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantEventProducer.class);

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
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishRestaurantValidationRequest(RestaurantValidationRequestEvent event) {
        if (event == null || event.getCorrelationId() == null || event.getRestaurantId() == null) {
            logger.error("Cannot publish null restaurant validation request event or event with null IDs");
            return false;
        }

        try {
            logger.info("Publishing restaurant validation request: correlationId={}, restaurantId={}",
                    event.getCorrelationId(), event.getRestaurantId());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESTAURANT_VALIDATION_REQUEST)
                    .setHeader(KafkaHeaders.KEY, event.getCorrelationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Restaurant validation request sent successfully: correlationId={}, offset={}",
                            event.getCorrelationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send restaurant validation request: correlationId={}, error={}",
                            event.getCorrelationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing restaurant validation request: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes a request to validate a reservation time based on restaurant hours.
     * This event is sent to the reservation time validation topic and is used
     * to verify if a requested reservation time falls within the restaurant's
     * operating hours.
     *
     * @param event The reservation time validation request event containing time details
     * @return true if the event was successfully sent, false otherwise
     */
    public boolean publishReservationTimeValidationRequest(ReservationTimeValidationRequestEvent event) {
        if (event == null || event.getCorrelationId() == null || event.getRestaurantId() == null) {
            logger.error("Cannot publish null reservation time validation request event or event with null IDs");
            return false;
        }

        try {
            logger.info("Publishing reservation time validation request: correlationId={}, restaurantId={}, time={}",
                    event.getCorrelationId(), event.getRestaurantId(), event.getReservationTime());

            Message<?> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, KafkaTopics.RESERVATION_TIME_VALIDATION_REQUEST)
                    .setHeader(KafkaHeaders.KEY, event.getCorrelationId())
                    .build();

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.debug("Reservation time validation request sent successfully: correlationId={}, offset={}",
                            event.getCorrelationId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Failed to send reservation time validation request: correlationId={}, error={}",
                            event.getCorrelationId(), ex.getMessage(), ex);
                }
            });

            return true;
        } catch (Exception e) {
            logger.error("Error publishing reservation time validation request: {}", e.getMessage(), e);
            return false;
        }
    }
}