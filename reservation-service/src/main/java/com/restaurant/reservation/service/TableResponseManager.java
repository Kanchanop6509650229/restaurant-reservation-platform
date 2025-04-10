package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;

/**
 * Manages asynchronous responses for table availability requests using a correlation-based pattern.
 * This component:
 * - Tracks pending table availability requests using correlation IDs
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
public class TableResponseManager {
    
    /** Logger for this component */
    private static final Logger logger = LoggerFactory.getLogger(TableResponseManager.class);
    
    /** Thread-safe map to store pending responses by correlation ID */
    private final Map<String, CompletableFuture<FindAvailableTableResponseEvent>> pendingResponses = new ConcurrentHashMap<>();
    
    /**
     * Creates a new pending response entry for a table availability request.
     * The response is stored in the pendingResponses map using the correlation ID as the key.
     *
     * @param correlationId unique identifier for the request-response pair
     * @return CompletableFuture that will be completed when the response is received
     */
    public CompletableFuture<FindAvailableTableResponseEvent> createPendingResponse(String correlationId) {
        CompletableFuture<FindAvailableTableResponseEvent> future = new CompletableFuture<>();
        pendingResponses.put(correlationId, future);
        return future;
    }
    
    /**
     * Completes a pending response when a table availability response event is received.
     * Removes the completed response from the pending responses map.
     *
     * @param response the table availability response event containing the result
     */
    public void completeResponse(FindAvailableTableResponseEvent response) {
        String correlationId = response.getCorrelationId();
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.remove(correlationId);
        
        if (future != null) {
            future.complete(response);
            logger.info("Completed response for correlationId: {}", correlationId);
        } else {
            logger.warn("Received response for unknown correlationId: {}", correlationId);
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
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.remove(correlationId);
        
        if (future != null) {
            future.completeExceptionally(new RuntimeException("Request cancelled: " + reason));
            logger.warn("Cancelled pending response for correlationId: {} - Reason: {}", correlationId, reason);
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
    }
    
    /**
     * Retrieves a response with a specified timeout period.
     * If the response is not received within the timeout period, the request is removed
     * and an exception is thrown.
     *
     * @param correlationId unique identifier for the request-response pair
     * @param timeout duration to wait for the response
     * @param unit time unit for the timeout duration
     * @return the table availability response event if received within the timeout period
     * @throws Exception if the timeout is exceeded or other errors occur
     */
    public FindAvailableTableResponseEvent getResponseWithTimeout(String correlationId, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<FindAvailableTableResponseEvent> future = pendingResponses.get(correlationId);
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
}