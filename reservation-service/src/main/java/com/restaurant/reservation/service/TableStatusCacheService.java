package com.restaurant.reservation.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.common.constants.StatusCodes;

/**
 * Service for caching table status to reduce calls to Restaurant Service.
 * This allows the Reservation Service to have a local copy of table statuses
 * that get updated via Kafka events.
 */
@Service
public class TableStatusCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(TableStatusCacheService.class);
    
    // Cache structure: Map<tableId, status>
    private final Map<String, String> tableStatusCache = new ConcurrentHashMap<>();
    
    /**
     * Get table status from cache
     * 
     * @param tableId the table ID
     * @return the status or null if not in cache
     */
    public String getTableStatus(String tableId) {
        return tableStatusCache.get(tableId);
    }
    
    /**
     * Update table status in cache
     * 
     * @param tableId the table ID
     * @param status the new status
     */
    public void updateTableStatus(String tableId, String status) {
        logger.debug("Updating table status in cache: {} -> {}", tableId, status);
        tableStatusCache.put(tableId, status);
    }
    
    /**
     * Check if a table is available
     * 
     * @param tableId the table ID
     * @return true if the table is available, false otherwise
     */
    public boolean isTableAvailable(String tableId) {
        String status = tableStatusCache.get(tableId);
        return status != null && status.equals(StatusCodes.TABLE_AVAILABLE);
    }
    
    /**
     * Remove table from cache
     * 
     * @param tableId the table ID
     */
    public void removeTableFromCache(String tableId) {
        tableStatusCache.remove(tableId);
    }
    
    /**
     * Get all cached table statuses
     * 
     * @return the map of table IDs to statuses
     */
    public Map<String, String> getAllTableStatuses() {
        return tableStatusCache;
    }
}