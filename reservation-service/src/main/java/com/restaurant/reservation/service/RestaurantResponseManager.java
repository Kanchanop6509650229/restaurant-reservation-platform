package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.restaurant.common.events.restaurant.RestaurantOwnershipResponseEvent;
import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;

/**
 * Manages asynchronous responses for restaurant validation requests using a correlation-based pattern.
 * This component:
 * - Tracks pending validation requests using correlation IDs
 * - Handles response completion and timeout scenarios
 * - Provides cleanup mechanisms for expired responses
 *
 * The manager uses CompletableFuture to handle asynchronous responses and ConcurrentHashMap
 * for thread-safe storage of pending requests.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class RestaurantResponseManager {

    /** Logger for this component */
    private static final Logger logger = LoggerFactory.getLogger(RestaurantResponseManager.class);

    /** Thread-safe map to store pending responses by correlation ID */
    private final Map<String, CompletableFuture<RestaurantValidationResponseEvent>> pendingResponses =
            new ConcurrentHashMap<>();

    /**
     * Creates a new pending response entry for a validation request.
     * The response is stored in the pendingResponses map using the correlation ID as the key.
     *
     * @param correlationId unique identifier for the request-response pair
     * @return CompletableFuture that will be completed when the response is received
     */
    public CompletableFuture<RestaurantValidationResponseEvent> createPendingResponse(String correlationId) {
        CompletableFuture<RestaurantValidationResponseEvent> future = new CompletableFuture<>();
        pendingResponses.put(correlationId, future);
        return future;
    }

    /**
     * Completes a pending response when a validation response event is received.
     * Removes the completed response from the pending responses map.
     *
     * @param response the validation response event containing the result
     */
    public void completeResponse(RestaurantValidationResponseEvent response) {
        String correlationId = response.getCorrelationId();
        CompletableFuture<RestaurantValidationResponseEvent> future = pendingResponses.remove(correlationId);

        if (future != null) {
            future.complete(response);
            logger.info("Completed restaurant validation response for correlationId: {}", correlationId);
        } else {
            logger.warn("Received restaurant validation response for unknown correlationId: {}", correlationId);
        }
    }

    /**
     * Cancels a pending response and removes it from the pending responses map.
     * The associated CompletableFuture is completed exceptionally with a RuntimeException.
     *
     * @param correlationId unique identifier for the request-response pair
     * @param reason description of why the response was cancelled
     */
    public void cancelPendingResponse(String correlationId, String reason) {
        CompletableFuture<RestaurantValidationResponseEvent> future = pendingResponses.remove(correlationId);

        if (future != null) {
            future.completeExceptionally(new RuntimeException("Request cancelled: " + reason));
            logger.warn("Cancelled pending restaurant validation response for correlationId: {} - Reason: {}",
                    correlationId, reason);
        }
    }

    /**
     * Retrieves a response with a specified timeout period.
     * If the response is not received within the timeout period, the request is removed
     * and an exception is thrown.
     *
     * @param correlationId unique identifier for the request-response pair
     * @param timeout duration to wait for the response
     * @param unit time unit for the timeout duration
     * @return the validation response event if received within the timeout period
     * @throws Exception if the timeout is exceeded or other errors occur
     */
    public RestaurantValidationResponseEvent getResponseWithTimeout(String correlationId, long timeout, TimeUnit unit)
            throws Exception {
        CompletableFuture<RestaurantValidationResponseEvent> future = pendingResponses.get(correlationId);
        if (future == null) {
            throw new IllegalArgumentException("No pending response for correlationId: " + correlationId);
        }

        try {
            return future.get(timeout, unit);
        } catch (Exception e) {
            pendingResponses.remove(correlationId);
            throw e;
        }
    }

    /**
     * Removes completed, cancelled, or exceptionally completed responses from the pending responses map.
     * This method should be called periodically to prevent memory leaks from abandoned requests.
     */
    public void cleanupExpiredResponses() {
        pendingResponses.forEach((correlationId, future) -> {
            if (future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
                pendingResponses.remove(correlationId);
            }
        });

        ownershipPendingResponses.forEach((correlationId, future) -> {
            if (future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
                ownershipPendingResponses.remove(correlationId);
            }
        });
    }

    /** Thread-safe map to store pending ownership validation responses by correlation ID */
    private final Map<String, CompletableFuture<RestaurantOwnershipResponseEvent>> ownershipPendingResponses =
            new ConcurrentHashMap<>();

    /**
     * Creates a new pending response entry for an ownership validation request.
     * The response is stored in the ownershipPendingResponses map using the correlation ID as the key.
     *
     * @param correlationId unique identifier for the request-response pair
     * @return CompletableFuture that will be completed when the response is received
     */
    public CompletableFuture<RestaurantOwnershipResponseEvent> createOwnershipPendingResponse(String correlationId) {
        CompletableFuture<RestaurantOwnershipResponseEvent> future = new CompletableFuture<>();
        ownershipPendingResponses.put(correlationId, future);
        return future;
    }

    /**
     * Completes a pending ownership validation response when a response event is received.
     * Removes the completed response from the pending responses map.
     *
     * @param response the ownership validation response event containing the result
     */
    public void completeOwnershipResponse(RestaurantOwnershipResponseEvent response) {
        String correlationId = response.getCorrelationId();
        CompletableFuture<RestaurantOwnershipResponseEvent> future = ownershipPendingResponses.remove(correlationId);

        if (future != null) {
            future.complete(response);
            logger.info("Completed restaurant ownership validation response for correlationId: {}", correlationId);
        } else {
            logger.warn("Received restaurant ownership validation response for unknown correlationId: {}", correlationId);
        }
    }

    /**
     * Retrieves an ownership validation response with a specified timeout period.
     * If the response is not received within the timeout period, the request is removed
     * and an exception is thrown.
     *
     * @param correlationId unique identifier for the request-response pair
     * @param timeout duration to wait for the response
     * @param unit time unit for the timeout duration
     * @return the ownership validation response event if received within the timeout period
     * @throws Exception if the timeout is exceeded or other errors occur
     */
    public RestaurantOwnershipResponseEvent getOwnershipResponseWithTimeout(String correlationId, long timeout, TimeUnit unit)
            throws Exception {
        CompletableFuture<RestaurantOwnershipResponseEvent> future = ownershipPendingResponses.get(correlationId);
        if (future == null) {
            throw new IllegalArgumentException("No pending ownership response for correlationId: " + correlationId);
        }

        try {
            return future.get(timeout, unit);
        } catch (Exception e) {
            ownershipPendingResponses.remove(correlationId);
            throw e;
        }
    }
}