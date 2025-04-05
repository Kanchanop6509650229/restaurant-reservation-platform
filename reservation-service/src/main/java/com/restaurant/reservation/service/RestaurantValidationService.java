package com.restaurant.reservation.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restaurant.common.events.restaurant.RestaurantValidationRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.kafka.producers.RestaurantEventProducer;

/**
 * Service for validating restaurant existence and operating hours via Kafka.
 */
@Service
public class RestaurantValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantValidationService.class);
    
    private final RestaurantEventProducer eventProducer;
    private final RestaurantResponseManager responseManager;
    
    @Value("${restaurant.validation.request.timeout:5}")
    private long requestTimeoutSeconds;
    
    public RestaurantValidationService(RestaurantEventProducer eventProducer, 
                                     RestaurantResponseManager responseManager) {
        this.eventProducer = eventProducer;
        this.responseManager = responseManager;
    }
    
    /**
     * Validates that a restaurant exists and is active.
     * 
     * @param restaurantId the restaurant ID to validate
     * @throws EntityNotFoundException if the restaurant doesn't exist
     * @throws ValidationException if there's an error during validation
     */
    public void validateRestaurantExists(String restaurantId) {
        // Generate correlation ID for this request
        String correlationId = UUID.randomUUID().toString();
        
        // Create a pending response to wait for
        responseManager.createPendingResponse(correlationId);
        
        try {
            // Create the request event
            RestaurantValidationRequestEvent requestEvent = new RestaurantValidationRequestEvent(
                    restaurantId, correlationId);
            
            logger.info("Sending restaurant validation request: correlationId={}, restaurantId={}", 
                    correlationId, restaurantId);
            
            // Send the request via Kafka
            eventProducer.publishRestaurantValidationRequest(requestEvent);
            
            // Wait for the response with timeout
            RestaurantValidationResponseEvent response = responseManager.getResponseWithTimeout(
                    correlationId, requestTimeoutSeconds, TimeUnit.SECONDS);
            
            if (response == null) {
                throw new ValidationException("restaurantId", 
                        "Failed to validate restaurant due to timeout");
            }
            
            if (!response.isExists()) {
                throw new EntityNotFoundException("Restaurant", restaurantId);
            }
            
            if (!response.isActive()) {
                throw new ValidationException("restaurantId", 
                        "The restaurant is currently not active");
            }
            
            logger.info("Restaurant validated successfully: restaurantId={}", restaurantId);
            
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for restaurant validation response: correlationId={}", 
                    correlationId);
            throw new ValidationException("restaurantId", 
                    "Restaurant validation timed out. Please try again later.");
        } catch (EntityNotFoundException e) {
            logger.error("Restaurant not found: restaurantId={}", restaurantId);
            throw e;
        } catch (Exception e) {
            logger.error("Error validating restaurant: {}", e.getMessage(), e);
            throw new ValidationException("restaurantId", 
                    "Failed to validate restaurant: " + e.getMessage());
        } finally {
            // Clean up pending response
            responseManager.cancelPendingResponse(correlationId, "Request completed or failed");
        }
    }
    
    /**
     * Validates that a specific time is within the restaurant's operating hours.
     * 
     * @param restaurantId the restaurant ID
     * @param reservationDateTime the date and time to validate
     * @throws ValidationException if the time is outside operating hours
     */
    public void validateOperatingHours(String restaurantId, java.time.LocalDateTime reservationDateTime) {
        // Implementation similar to validateRestaurantExists, but with time validation
        // We can extend this method to validate the reservation time against the restaurant's 
        // operating hours via Kafka
        // For now, we'll leave this as a placeholder for future implementation
    }
}