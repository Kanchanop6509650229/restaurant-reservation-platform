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
 * Kafka consumer that listens for restaurant validation requests and responds with validation status.
 */
@Component
public class RestaurantValidationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantValidationConsumer.class);
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventProducer restaurantEventProducer;
    
    public RestaurantValidationConsumer(
            RestaurantRepository restaurantRepository,
            RestaurantEventProducer restaurantEventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }
    
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