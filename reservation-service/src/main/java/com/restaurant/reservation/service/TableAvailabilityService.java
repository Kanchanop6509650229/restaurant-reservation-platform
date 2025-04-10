package com.restaurant.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.events.reservation.FindAvailableTableRequestEvent;
import com.restaurant.common.events.reservation.FindAvailableTableResponseEvent;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.domain.models.Reservation;
import com.restaurant.reservation.domain.repositories.ReservationRepository;
import com.restaurant.reservation.kafka.producers.ReservationEventProducer;

import jakarta.transaction.Transactional;

/**
 * Service responsible for managing table availability and assignments for reservations.
 * This service:
 * - Finds and assigns suitable tables for reservations
 * - Releases tables when reservations are cancelled or completed
 * - Manages table status through Kafka events
 * - Provides fallback REST-based table lookup
 * 
 * The service uses both asynchronous Kafka communication and synchronous REST calls
 * to ensure reliable table management.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class TableAvailabilityService {

    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityService.class);

    /** Repository for managing reservation data */
    private final ReservationRepository reservationRepository;
    
    /** Producer for publishing reservation-related events */
    private final ReservationEventProducer eventProducer;
    
    /** REST client for fallback table lookup */
    private final RestTemplate restTemplate;
    
    /** Service for caching table status */
    private final TableStatusCacheService tableStatusCacheService;
    
    /** Manager for handling table availability responses */
    private final TableResponseManager tableResponseManager;
    
    /** Timeout in seconds for table availability requests */
    @Value("${table.availability.request.timeout:10}")
    private long requestTimeoutSeconds;
    
    /** Base URL for the restaurant service REST API */
    @Value("${restaurant-service.url:http://localhost:8082}")
    private String restaurantServiceUrl;

    /**
     * Constructs a new TableAvailabilityService with required dependencies.
     *
     * @param reservationRepository Repository for reservation data
     * @param eventProducer Producer for reservation events
     * @param restTemplate REST client for HTTP requests
     * @param tableStatusCacheService Service for table status caching
     * @param tableResponseManager Manager for table responses
     */
    public TableAvailabilityService(ReservationRepository reservationRepository,
                                   ReservationEventProducer eventProducer,
                                   RestTemplate restTemplate,
                                   TableStatusCacheService tableStatusCacheService,
                                   TableResponseManager tableResponseManager) {
        this.reservationRepository = reservationRepository;
        this.eventProducer = eventProducer;
        this.restTemplate = restTemplate;
        this.tableStatusCacheService = tableStatusCacheService;
        this.tableResponseManager = tableResponseManager;
    }

    /**
     * Finds and assigns a suitable table for a reservation.
     * This method:
     * 1. Checks if a table is already assigned
     * 2. Verifies the reservation status
     * 3. Finds an available table through Kafka
     * 4. Updates the reservation with the assigned table
     * 5. Publishes a table status change event
     *
     * @param reservation the reservation needing a table
     */
    @Transactional
    public void findAndAssignTable(Reservation reservation) {
        // Skip if already assigned
        if (reservation.getTableId() != null) {
            return;
        }
        
        // Skip if not in PENDING or CONFIRMED status
        if (!reservation.getStatus().equals(StatusCodes.RESERVATION_PENDING) &&
            !reservation.getStatus().equals(StatusCodes.RESERVATION_CONFIRMED)) {
            return;
        }
        
        try {
            // Find suitable table using Kafka
            String tableId = findSuitableTableAsync(
                    reservation.getId(),
                    reservation.getRestaurantId(),
                    reservation.getReservationTime(),
                    reservation.getEndTime(),
                    reservation.getPartySize());
            
            if (tableId != null) {
                // Assign table to reservation
                reservation.setTableId(tableId);
                reservationRepository.save(reservation);
                
                // Publish table status changed event via Kafka
                publishTableStatusEvent(
                    tableId, 
                    reservation.getRestaurantId(),
                    getTableStatus(tableId), 
                    StatusCodes.TABLE_RESERVED, 
                    reservation.getId()
                );
                
                logger.info("Table assigned to reservation: tableId={}, reservationId={}", 
                        tableId, reservation.getId());
            } else {
                logger.warn("No suitable table found for reservation: {}", reservation.getId());
            }
        } catch (Exception e) {
            logger.error("Error finding and assigning table: {}", e.getMessage(), e);
        }
    }

    /**
     * Releases a table assigned to a reservation.
     * This method:
     * 1. Checks if the reservation has an assigned table
     * 2. Updates the table status to available
     * 3. Removes the table assignment from the reservation
     * 4. Updates the cache and publishes status change event
     *
     * @param reservation the reservation whose table should be released
     */
    @Transactional
    public void releaseTable(Reservation reservation) {
        // Skip if no table assigned
        if (reservation.getTableId() == null) {
            return;
        }
        
        String tableId = reservation.getTableId();
        String restaurantId = reservation.getRestaurantId();
        
        // Publish table status changed event via Kafka
        publishTableStatusEvent(
            tableId,
            restaurantId,
            getTableStatus(tableId),
            StatusCodes.TABLE_AVAILABLE,
            null
        );
        
        // Clear table assignment
        reservation.setTableId(null);
        reservationRepository.save(reservation);
        
        logger.info("Table released: tableId={}, reservationId={}", 
                tableId, reservation.getId());
    }

    /**
     * Finds a suitable table asynchronously through Kafka.
     * This method:
     * 1. Generates a correlation ID for the request
     * 2. Creates a pending response entry
     * 3. Publishes a find table request event
     * 4. Waits for the response with timeout
     * 5. Processes the response and returns the table ID
     *
     * @param reservationId ID of the reservation
     * @param restaurantId ID of the restaurant
     * @param startTime start time of the reservation
     * @param endTime end time of the reservation
     * @param partySize size of the party
     * @return ID of the suitable table, or null if none found
     * @throws Exception if the request times out or other errors occur
     */
    private String findSuitableTableAsync(String reservationId, String restaurantId, 
                                        LocalDateTime startTime, LocalDateTime endTime, 
                                        int partySize) throws Exception {
        // Generate a unique correlation ID for this request
        String correlationId = UUID.randomUUID().toString();
        
        // Create a CompletableFuture to wait for the response
        tableResponseManager.createPendingResponse(correlationId);
        
        try {
            // Create and send the request event
            FindAvailableTableRequestEvent requestEvent = new FindAvailableTableRequestEvent(
                    reservationId, 
                    restaurantId, 
                    startTime, 
                    endTime, 
                    partySize, 
                    correlationId);
            
            logger.info("Sending find available table request: correlationId={}, reservationId={}", 
                    correlationId, reservationId);
            
            eventProducer.publishFindAvailableTableRequest(requestEvent);
            
            // Wait for the response with timeout
            FindAvailableTableResponseEvent response = tableResponseManager.getResponseWithTimeout(
                    correlationId, requestTimeoutSeconds, TimeUnit.SECONDS);
            
            if (response != null && response.isSuccess()) {
                return response.getTableId();
            } else {
                String errorMsg = response != null ? response.getErrorMessage() : "No response received";
                logger.warn("Failed to find suitable table: {}", errorMsg);
                return null;
            }
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for table availability response: correlationId={}", correlationId);
            throw new ValidationException("tableId", "Timeout finding available table");
        } catch (Exception e) {
            logger.error("Error finding suitable table: {}", e.getMessage(), e);
            throw e;
        } finally {
            // Always clean up to avoid memory leaks
            tableResponseManager.cancelPendingResponse(correlationId, "Request completed or failed");
        }
    }

    /**
     * Gets the current status of a table.
     * First checks the cache, then defaults to available if not found.
     *
     * @param tableId ID of the table to check
     * @return current status of the table
     */
    private String getTableStatus(String tableId) {
        // First check the cache
        String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        
        // If not in cache, default to available
        return StatusCodes.TABLE_AVAILABLE;
    }
    
    /**
     * Publishes a table status change event via Kafka.
     * Updates the local cache and publishes the event to notify other services.
     *
     * @param tableId ID of the table
     * @param restaurantId ID of the restaurant
     * @param oldStatus previous status of the table
     * @param newStatus new status of the table
     * @param reservationId ID of the associated reservation, if any
     * @throws ValidationException if the status update fails
     */
    private void publishTableStatusEvent(String tableId, String restaurantId, String oldStatus, String newStatus, String reservationId) {
        try {
            // Create and publish the event
            TableStatusChangedEvent event = new TableStatusChangedEvent(
                restaurantId,
                tableId,
                oldStatus,
                newStatus,
                reservationId
            );
            
            // Update local cache immediately
            tableStatusCacheService.updateTableStatus(tableId, newStatus);
            
            // Publish via Kafka
            eventProducer.publishTableStatusChangedEvent(event);
        } catch (Exception e) {
            throw new ValidationException("tableId", 
                    "Failed to update table status: " + e.getMessage());
        }
    }
    
    /**
     * Fallback method to find a suitable table via REST API.
     * Used when Kafka-based lookup fails or times out.
     * This method:
     * 1. Calls the restaurant service REST API
     * 2. Processes the response to find available tables
     * 3. Checks table capacity and current status
     * 4. Verifies no conflicting reservations exist
     *
     * @param restaurantId ID of the restaurant
     * @param startTime start time of the reservation
     * @param endTime end time of the reservation
     * @param partySize size of the party
     * @return ID of the suitable table, or null if none found
     */
    private String findSuitableTableViaRest(String restaurantId, LocalDateTime startTime, 
                                    LocalDateTime endTime, int partySize) {
        try {
            // Call REST API to get available tables
            ResponseEntity<Map> response = restTemplate.exchange(
                    restaurantServiceUrl + "/api/restaurants/" + restaurantId + "/tables/available",
                    HttpMethod.GET,
                    null,
                    Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }
            
            // Extract tables from response
            List<Map<String, Object>> tables = (List<Map<String, Object>>) 
                    ((Map<String, Object>) response.getBody().get("data")).get("tables");
            
            if (tables == null || tables.isEmpty()) {
                return null;
            }
            
            // Check for conflicting reservations
            for (Map<String, Object> table : tables) {
                String tableId = (String) table.get("id");
                int capacity = (int) table.get("capacity");
                
                // Skip tables that are too small
                if (capacity < partySize) {
                    continue;
                }
                
                // Check if the table is available from the cache first
                String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
                if (cachedStatus != null && !cachedStatus.equals(StatusCodes.TABLE_AVAILABLE)) {
                    continue;
                }
                
                // Check if this table has conflicting reservations
                List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                        restaurantId, tableId, startTime, endTime);
                
                if (conflicts.isEmpty()) {
                    return tableId;
                }
            }
        } catch (Exception e) {
            logger.error("Error finding suitable table via REST: {}", e.getMessage(), e);
        }
        
        return null;
    }
}