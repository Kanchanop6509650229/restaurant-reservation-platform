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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
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
     * @throws IllegalArgumentException if the reservation is null
     */
    @Transactional
    public void findAndAssignTable(Reservation reservation) {
        if (reservation == null) {
            logger.error("Cannot assign table to null reservation");
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        // Skip if already assigned
        if (reservation.getTableId() != null) {
            logger.debug("Reservation {} already has table {} assigned",
                    reservation.getId(), reservation.getTableId());
            return;
        }

        // Skip if not in PENDING or CONFIRMED status
        if (!StatusCodes.RESERVATION_PENDING.equals(reservation.getStatus()) &&
            !StatusCodes.RESERVATION_CONFIRMED.equals(reservation.getStatus())) {
            logger.debug("Skipping table assignment for reservation {} with status {}",
                    reservation.getId(), reservation.getStatus());
            return;
        }

        try {
            // Find suitable table using Kafka
            logger.info("Finding suitable table for reservation {}, party size {}, time {}",
                    reservation.getId(), reservation.getPartySize(), reservation.getReservationTime());

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
     * @throws IllegalArgumentException if the reservation is null
     */
    @Transactional
    public void releaseTable(Reservation reservation) {
        if (reservation == null) {
            logger.error("Cannot release table from null reservation");
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        // Skip if no table assigned
        if (reservation.getTableId() == null) {
            logger.debug("No table to release for reservation {}", reservation.getId());
            return;
        }

        String tableId = reservation.getTableId();
        String restaurantId = reservation.getRestaurantId();

        logger.info("Releasing table {} for reservation {}", tableId, reservation.getId());

        // Publish table status changed event via Kafka
        publishTableStatusEvent(
            tableId,
            restaurantId,
            getTableStatus(tableId),
            StatusCodes.TABLE_AVAILABLE,
            null
        );

        // Update reservation to remove table assignment
        reservation.setTableId(null);
        reservationRepository.save(reservation);

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
     * 6. Falls back to REST API if Kafka fails
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
        // Validate input parameters
        if (reservationId == null || restaurantId == null || startTime == null || endTime == null || partySize <= 0) {
            logger.error("Invalid parameters for finding suitable table: reservationId={}, restaurantId={}, partySize={}",
                    reservationId, restaurantId, partySize);
            throw new IllegalArgumentException("Invalid parameters for finding suitable table");
        }

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

            logger.info("Sending find available table request: correlationId={}, reservationId={}, restaurantId={}, partySize={}",
                    correlationId, reservationId, restaurantId, partySize);

            eventProducer.publishFindAvailableTableRequest(requestEvent);

            // Wait for the response with timeout
            FindAvailableTableResponseEvent response = tableResponseManager.getResponseWithTimeout(
                    correlationId, requestTimeoutSeconds, TimeUnit.SECONDS);

            if (response != null && response.isSuccess()) {
                logger.info("Found suitable table {} for reservation {}", response.getTableId(), reservationId);
                return response.getTableId();
            } else {
                String errorMsg = response != null ? response.getErrorMessage() : "No response received";
                logger.warn("Failed to find suitable table via Kafka: {}", errorMsg);

                // Try fallback to REST API
                logger.info("Attempting fallback to REST API for finding suitable table");
                return findSuitableTableViaRest(restaurantId, startTime, endTime, partySize);
            }
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for table availability response: correlationId={}", correlationId);

            // Try fallback to REST API
            logger.info("Timeout occurred, attempting fallback to REST API for finding suitable table");
            return findSuitableTableViaRest(restaurantId, startTime, endTime, partySize);
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
     * @throws IllegalArgumentException if tableId is null or empty
     */
    private String getTableStatus(String tableId) {
        if (tableId == null || tableId.isEmpty()) {
            logger.error("Cannot get status for null or empty tableId");
            throw new IllegalArgumentException("Table ID cannot be null or empty");
        }

        // First check the cache
        String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
        if (cachedStatus != null) {
            logger.debug("Found cached status for table {}: {}", tableId, cachedStatus);
            return cachedStatus;
        }

        // If not in cache, default to available
        logger.debug("No cached status found for table {}, defaulting to AVAILABLE", tableId);
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
     * @throws IllegalArgumentException if tableId, restaurantId, or newStatus is null or empty
     */
    private void publishTableStatusEvent(String tableId, String restaurantId, String oldStatus, String newStatus, String reservationId) {
        // Validate input parameters
        if (tableId == null || tableId.isEmpty()) {
            throw new IllegalArgumentException("Table ID cannot be null or empty");
        }

        if (restaurantId == null || restaurantId.isEmpty()) {
            throw new IllegalArgumentException("Restaurant ID cannot be null or empty");
        }

        if (newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("New status cannot be null or empty");
        }

        try {
            logger.info("Publishing table status change: table={}, restaurant={}, oldStatus={}, newStatus={}, reservation={}",
                    tableId, restaurantId, oldStatus, newStatus, reservationId);

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

            logger.debug("Successfully published table status change event");
        } catch (Exception e) {
            logger.error("Failed to update table status: {}", e.getMessage(), e);
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
        if (restaurantId == null || startTime == null || endTime == null || partySize <= 0) {
            logger.error("Invalid parameters for REST table lookup: restaurantId={}, partySize={}",
                    restaurantId, partySize);
            return null;
        }

        try {
            logger.info("Attempting to find suitable table via REST API: restaurant={}, partySize={}, time={}",
                    restaurantId, partySize, startTime);

            // Call REST API to get available tables
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    restaurantServiceUrl + "/api/restaurants/" + restaurantId + "/tables/available",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.warn("Failed to get available tables from REST API: status={}",
                        response.getStatusCode());
                return null;
            }

            // Extract tables from response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                logger.warn("Empty response body from REST API");
                return null;
            }

            Object dataObj = responseBody.get("data");

            if (!(dataObj instanceof Map)) {
                logger.warn("Invalid data format in REST API response");
                return null;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tables = (List<Map<String, Object>>) data.get("tables");

            if (tables == null || tables.isEmpty()) {
                logger.info("No tables found in REST API response");
                return null;
            }

            logger.info("Found {} tables via REST API, checking for suitable match", tables.size());

            // Check for conflicting reservations
            for (Map<String, Object> table : tables) {
                String tableId = (String) table.get("id");
                Object capacityObj = table.get("capacity");

                if (tableId == null || capacityObj == null) {
                    logger.warn("Invalid table data in REST API response: {}", table);
                    continue;
                }

                int capacity;
                if (capacityObj instanceof Integer) {
                    capacity = (Integer) capacityObj;
                } else if (capacityObj instanceof Number) {
                    capacity = ((Number) capacityObj).intValue();
                } else {
                    logger.warn("Invalid capacity format in table data: {}", capacityObj);
                    continue;
                }

                // Skip tables that are too small
                if (capacity < partySize) {
                    logger.debug("Table {} capacity {} is too small for party size {}",
                            tableId, capacity, partySize);
                    continue;
                }

                // Check if the table is available from the cache first
                String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
                if (cachedStatus != null && !cachedStatus.equals(StatusCodes.TABLE_AVAILABLE)) {
                    logger.debug("Table {} is not available according to cache: {}",
                            tableId, cachedStatus);
                    continue;
                }

                // Check if this table has conflicting reservations
                List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                        restaurantId, tableId, startTime, endTime);

                if (conflicts.isEmpty()) {
                    logger.info("Found suitable table via REST API: {}", tableId);
                    return tableId;
                } else {
                    logger.debug("Table {} has {} conflicting reservations",
                            tableId, conflicts.size());
                }
            }

            logger.info("No suitable table found via REST API");
            return null;
        } catch (HttpClientErrorException | ResourceAccessException e) {
            logger.error("Error finding suitable table via REST: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error finding suitable table via REST: {}", e.getMessage(), e);
            return null;
        }
    }
}