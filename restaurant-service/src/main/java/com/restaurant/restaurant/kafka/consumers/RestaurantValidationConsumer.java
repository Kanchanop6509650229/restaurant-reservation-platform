package com.restaurant.restaurant.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

/**
 * Kafka consumer for processing restaurant validation requests.
 * This consumer handles:
 * - Restaurant existence verification
 * - Activity status validation
 * - Validation response publishing
 * - Error handling and reporting
 * 
 * Events are consumed from the restaurant validation request topic
 * and processed to validate restaurant availability for reservations
 * and other operations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantValidationConsumer {

    /** Logger for restaurant validation request processing */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantValidationConsumer.class);
    
    /** Repository for restaurant data access */
    private final RestaurantRepository restaurantRepository;
    
    /** Producer for publishing validation response events */
    private final RestaurantEventProducer restaurantEventProducer;
    
    /**
     * Constructs a new RestaurantValidationConsumer with required dependencies.
     *
     * @param restaurantRepository Repository for restaurant data access
     * @param restaurantEventProducer Producer for publishing response events
     */
    public RestaurantValidationConsumer(
            RestaurantRepository restaurantRepository,
            RestaurantEventProducer restaurantEventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }
    
    /**
     * Consumes and processes restaurant validation request events from Kafka.
     * This method:
     * - Verifies restaurant existence
     * - Checks restaurant active status
     * - Publishes validation response events
     * - Handles and reports errors
     *
     * @param event The restaurant validation request event
     */
    @KafkaListener(
            topics = KafkaTopics.RESTAURANT_VALIDATION_REQUEST,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "restaurantValidationKafkaListenerContainerFactory"
    )
    public void consumeRestaurantValidationRequest(RestaurantValidationRequestEvent event) {
        logger.info("Received restaurant validation request: correlationId={}, restaurantId={}", 
                event.getCorrelationId(), event.getRestaurantId());
        
        try {
            // Check if restaurant exists and is active
            Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId()).orElse(null);
            boolean exists = restaurant != null;
            boolean active = exists && restaurant.isActive();
            
            // Create and send response
            RestaurantValidationResponseEvent response = new RestaurantValidationResponseEvent(
                    event.getRestaurantId(),
                    event.getCorrelationId(),
                    exists,
                    active
            );
            
            if (!exists) {
                response.setErrorMessage("Restaurant not found");
            } else if (!active) {
                response.setErrorMessage("Restaurant is not active");
            }
            
            // Publish response via Kafka
            restaurantEventProducer.publishRestaurantValidationResponse(response);
            
            logger.info("Sent restaurant validation response: correlationId={}, exists={}, active={}",
                    event.getCorrelationId(), exists, active);
            
        } catch (Exception e) {
            logger.error("Error processing restaurant validation request: {}", e.getMessage(), e);
            
            // Send error response
            RestaurantValidationResponseEvent errorResponse = new RestaurantValidationResponseEvent(
                    event.getRestaurantId(),
                    event.getCorrelationId(),
                    false,
                    false
            );
            errorResponse.setErrorMessage("Error processing validation request: " + e.getMessage());
            
            restaurantEventProducer.publishRestaurantValidationResponse(errorResponse);
        }
    }
}