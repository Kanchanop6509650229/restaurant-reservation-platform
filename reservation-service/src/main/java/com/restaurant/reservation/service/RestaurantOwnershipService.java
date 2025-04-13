package com.restaurant.reservation.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restaurant.common.events.restaurant.RestaurantOwnershipRequestEvent;
import com.restaurant.common.events.restaurant.RestaurantOwnershipResponseEvent;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.kafka.producers.RestaurantEventProducer;

/**
 * Service responsible for validating restaurant ownership through Kafka-based communication.
 * This service handles checking if a user is the owner of a specific restaurant.
 *
 * The service uses a request-response pattern over Kafka to communicate with the restaurant service,
 * with built-in timeout handling and error management.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class RestaurantOwnershipService {

    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnershipService.class);

    /** Producer for sending validation requests to Kafka */
    private final RestaurantEventProducer eventProducer;

    /** Manager for handling validation responses */
    private final RestaurantResponseManager responseManager;

    /** Timeout in seconds for waiting for validation responses */
    @Value("${restaurant.validation.request.timeout:5}")
    private long requestTimeoutSeconds;

    /**
     * Constructs a new RestaurantOwnershipService with required dependencies.
     *
     * @param eventProducer Producer for sending validation requests
     * @param responseManager Manager for handling validation responses
     */
    public RestaurantOwnershipService(RestaurantEventProducer eventProducer,
            RestaurantResponseManager responseManager) {
        this.eventProducer = eventProducer;
        this.responseManager = responseManager;
    }

    /**
     * Checks if a user is the owner of a restaurant.
     * This method:
     * 1. Generates a correlation ID for the request
     * 2. Creates a pending response entry
     * 3. Sends an ownership validation request via Kafka
     * 4. Waits for the response with timeout
     * 5. Validates the response
     * 6. Cleans up the pending response
     *
     * @param restaurantId the restaurant ID to check
     * @param userId the user ID to check ownership for
     * @return true if the user is the owner, false otherwise
     * @throws ValidationException if there's an error during validation or timeout occurs
     */
    public boolean isUserRestaurantOwner(String restaurantId, String userId) {
        // Generate correlation ID for this request
        String correlationId = UUID.randomUUID().toString();

        // Create a pending response to wait for
        responseManager.createOwnershipPendingResponse(correlationId);

        try {
            // Create the request event
            RestaurantOwnershipRequestEvent requestEvent = new RestaurantOwnershipRequestEvent(
                    restaurantId, userId, correlationId);

            logger.info("Sending restaurant ownership validation request: correlationId={}, restaurantId={}, userId={}",
                    correlationId, restaurantId, userId);

            // Send the request via Kafka
            eventProducer.publishRestaurantOwnershipRequest(requestEvent);

            // Wait for the response with timeout
            RestaurantOwnershipResponseEvent response = responseManager.getOwnershipResponseWithTimeout(
                    correlationId, requestTimeoutSeconds, TimeUnit.SECONDS);

            if (response == null) {
                logger.error("Timeout waiting for restaurant ownership validation response: correlationId={}",
                        correlationId);
                return false;
            }

            logger.info("Restaurant ownership validation response received: isOwner={}", response.isOwner());
            return response.isOwner();

        } catch (TimeoutException e) {
            logger.error("Timeout waiting for restaurant ownership validation response: correlationId={}",
                    correlationId);
            return false;
        } catch (Exception e) {
            logger.error("Error validating restaurant ownership: {}", e.getMessage(), e);
            return false;
        } finally {
            // Clean up pending response
            responseManager.cancelPendingResponse(correlationId, "Request completed or failed");
        }
    }
}
