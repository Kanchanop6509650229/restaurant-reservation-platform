package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.common.constants.StatusCodes;

/**
 * Service responsible for managing an in-memory cache of table statuses.
 * This service:
 * - Provides fast access to table status information
 * - Reduces load on the Restaurant Service by caching statuses locally
 * - Maintains consistency through Kafka event-based updates
 * 
 * The cache is implemented using a thread-safe ConcurrentHashMap and is updated
 * whenever table status changes are received via Kafka events.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class TableStatusCacheService {
    
    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(TableStatusCacheService.class);
    
    /** Thread-safe map to store table statuses by table ID */
    private final Map<String, String> tableStatusCache = new ConcurrentHashMap<>();
    
    /**
     * Retrieves the current status of a table from the cache.
     * Returns null if the table's status is not cached.
     *
     * @param tableId the unique identifier of the table
     * @return the current status of the table, or null if not found in cache
     */
    public String getTableStatus(String tableId) {
        return tableStatusCache.get(tableId);
    }
    
    /**
     * Updates the status of a table in the cache.
     * This method is typically called when a TableStatusChangedEvent is received.
     *
     * @param tableId the unique identifier of the table
     * @param status the new status to set for the table
     */
    public void updateTableStatus(String tableId, String status) {
        logger.debug("Updating table status in cache: {} -> {}", tableId, status);
        tableStatusCache.put(tableId, status);
    }
    
    /**
     * Checks if a table is currently available for reservations.
     * A table is considered available if its status matches StatusCodes.TABLE_AVAILABLE.
     *
     * @param tableId the unique identifier of the table
     * @return true if the table is available, false if it's not available or not found in cache
     */
    public boolean isTableAvailable(String tableId) {
        String status = tableStatusCache.get(tableId);
        return status != null && status.equals(StatusCodes.TABLE_AVAILABLE);
    }
    
    /**
     * Removes a table's status from the cache.
     * This method is typically called when a table is deleted or when the cache entry becomes stale.
     *
     * @param tableId the unique identifier of the table to remove from cache
     */
    public void removeTableFromCache(String tableId) {
        tableStatusCache.remove(tableId);
    }
    
    /**
     * Retrieves all cached table statuses.
     * This method returns a direct reference to the cache map, so callers should
     * treat the returned map as read-only to maintain cache consistency.
     *
     * @return an unmodifiable view of the current table status cache
     */
    public Map<String, String> getAllTableStatuses() {
        return tableStatusCache;
    }
}