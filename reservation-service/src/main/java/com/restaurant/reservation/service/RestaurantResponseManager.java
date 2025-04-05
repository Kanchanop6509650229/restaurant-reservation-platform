package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.restaurant.common.events.restaurant.RestaurantValidationResponseEvent;

/**
 * Manages asynchronous responses for restaurant validation requests.
 */
@Component
public class RestaurantResponseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantResponseManager.class);
    
    // Map to store pending responses by correlation ID
    private final Map<String, CompletableFuture<RestaurantValidationResponseEvent>> pendingResponses = 
            new ConcurrentHashMap<>();
    
    /**
     * Creates a pending response for the given correlation ID.
     */
    public CompletableFuture<RestaurantValidationResponseEvent> createPendingResponse(String correlationId) {
        CompletableFuture<RestaurantValidationResponseEvent> future = new CompletableFuture<>();
        pendingResponses.put(correlationId, future);
        return future;
    }
    
    /**
     * Completes a pending response when a Kafka event is received.
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
     * Cancels a pending response.
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
     * Gets a response with timeout.
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
     * Cleans up expired responses.
     */
    public void cleanupExpiredResponses() {
        pendingResponses.forEach((correlationId, future) -> {
            if (future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
                pendingResponses.remove(correlationId);
            }
        });
    }
}