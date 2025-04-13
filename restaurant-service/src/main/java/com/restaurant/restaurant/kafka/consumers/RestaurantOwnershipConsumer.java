package com.restaurant.restaurant.kafka.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.restaurant.common.constants.KafkaTopics;
import com.restaurant.common.events.restaurant.RestaurantOwnershipRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantOwnershipResponseEvent;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

/**
 * Kafka consumer for restaurant ownership validation requests.
 * This consumer listens for ownership validation requests and checks if a user
 * is the owner of a specific restaurant.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantOwnershipConsumer {
    
    /** Logger for this consumer */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnershipConsumer.class);
    
    /** Repository for restaurant data access */
    private final RestaurantRepository restaurantRepository;
    
    /** Producer for sending validation responses */
    private final RestaurantEventProducer eventProducer;
    
    /**
     * Constructs a new RestaurantOwnershipConsumer with required dependencies.
     *
     * @param restaurantRepository Repository for restaurant data access
     * @param eventProducer Producer for sending validation responses
     */
    public RestaurantOwnershipConsumer(RestaurantRepository restaurantRepository, RestaurantEventProducer eventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.eventProducer = eventProducer;
    }
    
    /**
     * Listens for restaurant ownership validation requests.
     * Checks if the specified user is the owner of the restaurant and sends a response.
     *
     * @param event The ownership validation request event
     */
    @KafkaListener(topics = KafkaTopics.RESTAURANT_OWNERSHIP_REQUEST, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOwnershipRequest(RestaurantOwnershipRequestEvent event) {
        if (event == null || event.getCorrelationId() == null || event.getRestaurantId() == null || event.getUserId() == null) {
            logger.error("Received null ownership request or request with null IDs");
            return;
        }
        
        logger.info("Received restaurant ownership validation request: correlationId={}, restaurantId={}, userId={}",
                event.getCorrelationId(), event.getRestaurantId(), event.getUserId());
        
        try {
            // Check if the user is the owner of the restaurant
            Restaurant restaurant = restaurantRepository.findById(event.getRestaurantId()).orElse(null);
            boolean isOwner = false;
            
            if (restaurant != null) {
                isOwner = restaurant.getOwnerId().equals(event.getUserId());
            }
            
            // Create and send response
            RestaurantOwnershipResponseEvent response = new RestaurantOwnershipResponseEvent(
                    event.getRestaurantId(),
                    event.getUserId(),
                    event.getCorrelationId(),
                    isOwner
            );
            
            if (restaurant == null) {
                response.setErrorMessage("Restaurant not found");
            }
            
            eventProducer.publishRestaurantOwnershipResponse(response);
            logger.info("Sent restaurant ownership validation response: correlationId={}, isOwner={}",
                    event.getCorrelationId(), isOwner);
            
        } catch (Exception e) {
            logger.error("Error processing restaurant ownership validation request: {}", e.getMessage(), e);
            
            // Send error response
            RestaurantOwnershipResponseEvent errorResponse = new RestaurantOwnershipResponseEvent(
                    event.getRestaurantId(),
                    event.getUserId(),
                    event.getCorrelationId(),
                    false
            );
            errorResponse.setErrorMessage("Error processing request: " + e.getMessage());
            
            eventProducer.publishRestaurantOwnershipResponse(errorResponse);
        }
    }
}
