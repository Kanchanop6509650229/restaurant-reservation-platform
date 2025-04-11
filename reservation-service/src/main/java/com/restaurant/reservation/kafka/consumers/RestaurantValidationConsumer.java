package com.restaurant.reservation.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.ReservationTimeValidationResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.reservation.service.RestaurantResponseManager;

/**
 * Kafka consumer for restaurant validation responses in the reservation service.
 * This class handles responses to restaurant validation requests and reservation
 * time validation requests, ensuring that reservations are only made for valid
 * restaurants and during valid operating hours.
 *
 * The consumer processes two types of responses:
 * 1. Restaurant validation responses - confirming restaurant existence
 * 2. Reservation time validation responses - confirming valid reservation times
 *
 * Responses are processed through the RestaurantResponseManager to complete
 * asynchronous validation requests.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantValidationConsumer {

    /** Logger instance for tracking validation responses */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantValidationConsumer.class);

    /** Manager for handling restaurant validation responses */
    private final RestaurantResponseManager responseManager;

    /**
     * Constructs a new RestaurantValidationConsumer with the specified response manager.
     *
     * @param responseManager The manager for handling validation responses
     */
    public RestaurantValidationConsumer(RestaurantResponseManager responseManager) {
        this.responseManager = responseManager;
    }

    /**
     * Consumes restaurant validation response events from the Kafka topic.
     * This method processes responses to restaurant validation requests,
     * logging the response details and completing the corresponding
     * CompletableFuture in the response manager.
     *
     * @param event The restaurant validation response event
     */
    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_VALIDATION_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantValidationKafkaListenerContainerFactory"
    )
    public void consumeRestaurantValidationResponse(RestaurantValidationResponseEvent event) {
        if (event == null) {
            logger.warn("Received null restaurant validation response event");
            return;
        }

        logger.info("Received restaurant validation response: correlationId={}, restaurantId={}, exists={}, active={}, errorMessage={}",
                event.getCorrelationId(),
                event.getRestaurantId(),
                event.isExists(),
                event.isActive(),
                event.getErrorMessage() != null ? event.getErrorMessage() : "none");

        try {
            // Pass the response to the manager to complete the CompletableFuture
            responseManager.completeResponse(event);
            logger.debug("Processed restaurant validation response for correlationId={}", event.getCorrelationId());
        } catch (Exception e) {
            logger.error("Error processing restaurant validation response: correlationId={}, error={}",
                    event.getCorrelationId(), e.getMessage(), e);
        }
    }

    /**
     * Consumes reservation time validation response events from the Kafka topic.
     * This method processes responses to reservation time validation requests,
     * converting them to restaurant validation responses and completing the
     * corresponding CompletableFuture in the response manager.
     *
     * @param event The reservation time validation response event
     */
    @KafkaListener(
            topics = KafkaTopics.RESERVATION_TIME_VALIDATION_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "reservationTimeValidationKafkaListenerContainerFactory"
    )
    public void consumeReservationTimeValidationResponse(ReservationTimeValidationResponseEvent event) {
        if (event == null) {
            logger.warn("Received null reservation time validation response event");
            return;
        }

        logger.info("Received reservation time validation response: correlationId={}, restaurantId={}, valid={}, errorMessage={}",
                event.getCorrelationId(),
                event.getRestaurantId(),
                event.isValid(),
                event.getErrorMessage() != null ? event.getErrorMessage() : "none");

        try {
            // Convert to RestaurantValidationResponseEvent since that's what our response manager expects
            RestaurantValidationResponseEvent responseEvent = new RestaurantValidationResponseEvent(
                event.getRestaurantId(),
                event.getCorrelationId(),
                true,  // Always set exists to true as we're handling time validation only
                true   // Set active to true by default
            );

            // If time validation failed, set the error message
            if (!event.isValid()) {
                responseEvent.setErrorMessage(event.getErrorMessage());
            }

            // Pass the converted response to the manager to complete the CompletableFuture
            responseManager.completeResponse(responseEvent);
            logger.debug("Processed time validation response for correlationId={}", event.getCorrelationId());
        } catch (Exception e) {
            logger.error("Error processing reservation time validation response: correlationId={}, error={}",
                    event.getCorrelationId(), e.getMessage(), e);
        }
    }
}