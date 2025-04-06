package com.restaurant.reservation.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.restaurant.common.events.restaurant.ReservationTimeValidationRequestEvent;
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
     * @throws ValidationException     if there's an error during validation
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
     * @param restaurantId        the restaurant ID
     * @param reservationDateTime the date and time to validate
     * @throws ValidationException if the time is outside operating hours
     */
    public void validateOperatingHours(String restaurantId, java.time.LocalDateTime reservationDateTime) {
        // Generate correlation ID for this request
        String correlationId = UUID.randomUUID().toString();

        // Create a pending response to wait for
        responseManager.createPendingResponse(correlationId);

        try {
            // Create the request event
            ReservationTimeValidationRequestEvent requestEvent = new ReservationTimeValidationRequestEvent(
                    restaurantId, correlationId, reservationDateTime.toString());

            logger.info("Sending reservation time validation request: correlationId={}, restaurantId={}, time={}",
                    correlationId, restaurantId, reservationDateTime);

            // Send the request via Kafka
            eventProducer.publishReservationTimeValidationRequest(requestEvent);

            // Wait for the response with timeout
            RestaurantValidationResponseEvent response = responseManager.getResponseWithTimeout(
                    correlationId, requestTimeoutSeconds, TimeUnit.SECONDS);

            if (response == null) {
                throw new ValidationException("reservationTime",
                        "Failed to validate reservation time due to timeout");
            }

            // Check if the restaurant exists (this should be already validated by
            // validateRestaurantExists)
            if (!response.isExists()) {
                throw new EntityNotFoundException("Restaurant", restaurantId);
            }

            // Check if the restaurant is active
            if (!response.isActive()) {
                throw new ValidationException("restaurantId",
                        "The restaurant is currently not active");
            }

            // Check if the time is valid (based on the error message)
            if (response.getErrorMessage() != null &&
                    response.getErrorMessage().contains("outside operating hours")) {
                throw new ValidationException("reservationTime",
                        response.getErrorMessage());
            }

            // Check if there's any other error
            if (response.getErrorMessage() != null) {
                throw new ValidationException("reservationTime",
                        response.getErrorMessage());
            }

            logger.info("Reservation time validated successfully: restaurantId={}, time={}",
                    restaurantId, reservationDateTime);

        } catch (TimeoutException e) {
            logger.error("Timeout waiting for reservation time validation response: correlationId={}",
                    correlationId);
            throw new ValidationException("reservationTime",
                    "Reservation time validation timed out. Please try again later.");
        } catch (EntityNotFoundException | ValidationException e) {
            logger.error("Error validating reservation time: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error validating reservation time: {}", e.getMessage(), e);
            throw new ValidationException("reservationTime",
                    "Failed to validate reservation time: " + e.getMessage());
        } finally {
            // Clean up pending response
            responseManager.cancelPendingResponse(correlationId, "Request completed or failed");
        }
    }
}