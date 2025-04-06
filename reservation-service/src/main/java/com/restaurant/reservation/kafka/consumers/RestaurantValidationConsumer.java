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
 * Kafka consumer for restaurant validation responses.
 */
@Component
public class RestaurantValidationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantValidationConsumer.class);
    
    private final RestaurantResponseManager responseManager;
    
    public RestaurantValidationConsumer(RestaurantResponseManager responseManager) {
        this.responseManager = responseManager;
    }
    
    /**
     * Consumes restaurant validation response events.
     */
    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_VALIDATION_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantValidationKafkaListenerContainerFactory"
    )
    public void consumeRestaurantValidationResponse(RestaurantValidationResponseEvent event) {
        logger.info("Received restaurant validation response: correlationId={}, restaurantId={}, exists={}", 
                event.getCorrelationId(), event.getRestaurantId(), event.isExists());
        
        try {
            // Pass the response to the manager to complete the CompletableFuture
            responseManager.completeResponse(event);
        } catch (Exception e) {
            logger.error("Error processing restaurant validation response: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Consumes reservation time validation response events.
     */
    @KafkaListener(
            topics = KafkaTopics.RESERVATION_TIME_VALIDATION_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "reservationTimeValidationKafkaListenerContainerFactory"
    )
    public void consumeReservationTimeValidationResponse(ReservationTimeValidationResponseEvent event) {
        logger.info("Received reservation time validation response: correlationId={}, restaurantId={}, valid={}", 
                event.getCorrelationId(), event.getRestaurantId(), event.isValid());
        
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
        } catch (Exception e) {
            logger.error("Error processing reservation time validation response: {}", e.getMessage(), e);
        }
    }
}