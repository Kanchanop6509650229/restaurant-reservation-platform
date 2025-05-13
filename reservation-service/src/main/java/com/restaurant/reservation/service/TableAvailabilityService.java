package com.restaurant.reservation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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

            String tableIdResult = findSuitableTableAsync(
                    reservation.getId(),
                    reservation.getRestaurantId(),
                    reservation.getReservationTime(),
                    reservation.getEndTime(),
                    reservation.getPartySize());

            if (tableIdResult != null) {
                // Check if this is a combined table result (contains commas)
                if (tableIdResult.contains(",")) {
                    // This is a combined table result
                    List<String> tableIds = Arrays.stream(tableIdResult.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

                    // Assign tables to reservation
                    reservation.setTableIds(tableIds);
                    reservationRepository.save(reservation);

                    // Publish table status changed events for all tables
                    for (String tableId : tableIds) {
                        publishTableStatusEvent(
                            tableId,
                            reservation.getRestaurantId(),
                            getTableStatus(tableId),
                            StatusCodes.TABLE_RESERVED,
                            reservation.getId()
                        );
                    }

                    logger.info("Combined tables assigned to reservation: tableIds={}, reservationId={}, partySize={}",
                            tableIdResult, reservation.getId(), reservation.getPartySize());
                } else {
                    // This is a single table result
                    // Assign table to reservation
                    reservation.setTableId(tableIdResult);
                    reservationRepository.save(reservation);

                    // Publish table status changed event via Kafka
                    publishTableStatusEvent(
                        tableIdResult,
                        reservation.getRestaurantId(),
                        getTableStatus(tableIdResult),
                        StatusCodes.TABLE_RESERVED,
                        reservation.getId()
                    );

                    logger.info("Table assigned to reservation: tableId={}, reservationId={}, partySize={}",
                            tableIdResult, reservation.getId(), reservation.getPartySize());
                }
            } else {
                logger.warn("No suitable table found for reservation: {}, partySize={}, time={}",
                        reservation.getId(), reservation.getPartySize(), reservation.getReservationTime());
            }
        } catch (Exception e) {
            logger.error("Error finding and assigning table: {}", e.getMessage(), e);
        }
    }

    /**
     * Releases tables assigned to a reservation.
     * This method:
     * 1. Checks if the reservation has assigned tables
     * 2. Updates the table status to available for all assigned tables
     * 3. Removes the table assignments from the reservation
     * 4. Updates the cache and publishes status change events
     *
     * @param reservation the reservation whose tables should be released
     * @throws IllegalArgumentException if the reservation is null
     */
    @Transactional
    public void releaseTable(Reservation reservation) {
        if (reservation == null) {
            logger.error("Cannot release tables from null reservation");
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        String restaurantId = reservation.getRestaurantId();

        // Check if this is a combined table reservation
        if (reservation.hasCombinedTables()) {
            List<String> tableIds = reservation.getTableIds();
            if (tableIds.isEmpty()) {
                logger.debug("No tables to release for reservation {}", reservation.getId());
                return;
            }

            logger.info("Releasing combined tables {} for reservation {}",
                    reservation.getCombinedTableIds(), reservation.getId());

            // Update table status to available for all tables
            for (String tableId : tableIds) {
                publishTableStatusEvent(
                    tableId,
                    restaurantId,
                    getTableStatus(tableId),
                    StatusCodes.TABLE_AVAILABLE,
                    null
                );
            }

            // Remove table assignments from reservation
            reservation.setTableIds(null);
            reservationRepository.save(reservation);

            logger.info("Combined tables released from reservation: tableIds={}, reservationId={}",
                    String.join(",", tableIds), reservation.getId());
        } else {
            // Single table reservation
            String tableId = reservation.getTableId();
            if (tableId == null) {
                logger.debug("No table to release for reservation {}", reservation.getId());
                return;
            }

            logger.info("Releasing table {} for reservation {}", tableId, reservation.getId());

            // Publish table status changed event via Kafka
            publishTableStatusEvent(
                tableId,
                restaurantId,
                getTableStatus(tableId),
                StatusCodes.TABLE_AVAILABLE,
                null
            );

            // Remove table assignment from reservation
            reservation.setTableId(null);
            reservationRepository.save(reservation);

            logger.info("Table released: tableId={}, reservationId={}",
                    tableId, reservation.getId());
        }
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
                // Check if this is a combined table result (contains multiple table IDs)
                if (response.getTableIds() != null && response.getTableIds().size() > 1) {
                    // For combined tables, return the comma-separated list of table IDs
                    String tableIds = String.join(",", response.getTableIds());
                    logger.info("Found combined tables {} for reservation {}", tableIds, reservationId);
                    return tableIds;
                } else {
                    // For a single table, return the table ID
                    logger.info("Found suitable table {} for reservation {}", response.getTableId(), reservationId);
                    return response.getTableId();
                }
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
    public String getTableStatus(String tableId) {
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
    public void publishTableStatusEvent(String tableId, String restaurantId, String oldStatus, String newStatus, String reservationId) {
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
     * Finds a suitable table for a reservation without assigning it.
     * This method is used to check if a table is available before committing to a reservation update.
     *
     * @param reservation the reservation to find a table for
     * @return ID of the suitable table, or null if none found
     */
    public String findSuitableTableForReservation(Reservation reservation) {
        if (reservation == null) {
            logger.error("Cannot find table for null reservation");
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        try {
            return findSuitableTableAsync(
                    reservation.getId(),
                    reservation.getRestaurantId(),
                    reservation.getReservationTime(),
                    reservation.getEndTime(),
                    reservation.getPartySize());
        } catch (Exception e) {
            logger.error("Error finding suitable table for reservation: {}", e.getMessage(), e);
            return null;
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
     * 5. Supports combining tables with 'combinable' property for larger party sizes
     *
     * @param restaurantId ID of the restaurant
     * @param startTime start time of the reservation
     * @param endTime end time of the reservation
     * @param partySize size of the party
     * @return ID of the suitable table, or comma-separated list of table IDs if combined, or null if none found
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
                    restaurantServiceUrl + "/api/restaurants/" + restaurantId + "/tables/public",
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

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tables = (List<Map<String, Object>>) responseBody.get("data");

            if (tables == null || tables.isEmpty()) {
                logger.info("No tables found in REST API response");
                return null;
            }

            logger.info("Found {} tables via REST API, checking for suitable match", tables.size());

            // First, try to find a single table that can accommodate the party
            String singleTableId = findSingleTableViaRest(tables, restaurantId, startTime, endTime, partySize);
            if (singleTableId != null) {
                logger.info("Found single suitable table via REST API: {}", singleTableId);
                return singleTableId;
            }

            // If no single table is found, try to find a combination of tables
            logger.info("No single table found for party size {}, trying to find combination of tables", partySize);

            // First, try with combinable tables only
            List<String> combinedTableIds = findCombinableTablesViaRest(tables, restaurantId, startTime, endTime, partySize, true);

            // If no combination of combinable tables is found, try with all tables
            if (combinedTableIds.isEmpty()) {
                logger.info("No combination of combinable tables found, trying with all tables");
                combinedTableIds = findCombinableTablesViaRest(tables, restaurantId, startTime, endTime, partySize, false);
            }

            if (!combinedTableIds.isEmpty()) {
                String result = String.join(",", combinedTableIds);
                logger.info("Found combination of tables for party size {}: {}", partySize, result);
                return result;
            }

            logger.info("No suitable table or combination found via REST API");
            return null;
        } catch (HttpClientErrorException | ResourceAccessException e) {
            logger.error("Error finding suitable table via REST: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error finding suitable table via REST: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Finds a single table that can accommodate the given party size.
     *
     * @param tables List of available tables from the REST API
     * @param restaurantId ID of the restaurant
     * @param startTime start time of the reservation
     * @param endTime end time of the reservation
     * @param partySize size of the party
     * @return ID of the suitable table, or null if none found
     */
    private String findSingleTableViaRest(List<Map<String, Object>> tables, String restaurantId,
                                         LocalDateTime startTime, LocalDateTime endTime, int partySize) {
        // Filter and sort tables by capacity (ascending) to find the smallest suitable table
        List<Map<String, Object>> suitableTables = tables.stream()
            .filter(table -> {
                // Extract table data
                String tableId = (String) table.get("id");
                Object capacityObj = table.get("capacity");

                if (tableId == null || capacityObj == null) {
                    logger.warn("Invalid table data in REST API response: {}", table);
                    return false;
                }

                int capacity;
                if (capacityObj instanceof Integer) {
                    capacity = (Integer) capacityObj;
                } else if (capacityObj instanceof Number) {
                    capacity = ((Number) capacityObj).intValue();
                } else {
                    logger.warn("Invalid capacity format in table data: {}", capacityObj);
                    return false;
                }

                // Check if table is large enough
                if (capacity < partySize) {
                    return false;
                }

                // Check if table is available in cache
                String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
                if (cachedStatus != null && !cachedStatus.equals(StatusCodes.TABLE_AVAILABLE)) {
                    return false;
                }

                // Check for conflicting reservations
                List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                        restaurantId, tableId, startTime, endTime);

                return conflicts.isEmpty();
            })
            .sorted((t1, t2) -> {
                int capacity1 = ((Number) t1.get("capacity")).intValue();
                int capacity2 = ((Number) t2.get("capacity")).intValue();
                return Integer.compare(capacity1, capacity2);
            })
            .collect(Collectors.toList());

        if (!suitableTables.isEmpty()) {
            Map<String, Object> selectedTable = suitableTables.get(0);
            return (String) selectedTable.get("id");
        }

        return null;
    }

    /**
     * Finds a combination of tables that can accommodate the given party size.
     *
     * @param tables List of available tables from the REST API
     * @param restaurantId ID of the restaurant
     * @param startTime start time of the reservation
     * @param endTime end time of the reservation
     * @param partySize size of the party
     * @param combinableOnly Whether to consider only tables with combinable=true
     * @return List of table IDs that can be combined, or empty list if no combination is found
     */
    private List<String> findCombinableTablesViaRest(List<Map<String, Object>> tables, String restaurantId,
                                                   LocalDateTime startTime, LocalDateTime endTime,
                                                   int partySize, boolean combinableOnly) {
        // Filter tables based on availability and combinable flag
        List<Map<String, Object>> eligibleTables = tables.stream()
            .filter(table -> {
                String tableId = (String) table.get("id");

                // Skip tables with invalid data
                if (tableId == null) {
                    return false;
                }

                // Check combinable flag if needed
                if (combinableOnly) {
                    Object combinableObj = table.get("combinable");
                    if (!(combinableObj instanceof Boolean) || !((Boolean) combinableObj)) {
                        return false;
                    }
                }

                // Check if table is available in cache
                String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
                if (cachedStatus != null && !cachedStatus.equals(StatusCodes.TABLE_AVAILABLE)) {
                    return false;
                }

                // Check for conflicting reservations
                List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                        restaurantId, tableId, startTime, endTime);

                return conflicts.isEmpty();
            })
            .collect(Collectors.toList());

        if (eligibleTables.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort tables by capacity (descending) to minimize the number of tables needed
        eligibleTables.sort((t1, t2) -> {
            int capacity1 = getTableCapacity(t1);
            int capacity2 = getTableCapacity(t2);
            return Integer.compare(capacity2, capacity1);
        });

        List<Map<String, Object>> selectedTables = new ArrayList<>();
        int totalCapacity = 0;

        // Select tables until we have enough capacity
        for (Map<String, Object> table : eligibleTables) {
            selectedTables.add(table);
            totalCapacity += getTableCapacity(table);

            if (totalCapacity >= partySize) {
                break;
            }
        }

        // If we couldn't get enough capacity, return empty list
        if (totalCapacity < partySize) {
            return Collections.emptyList();
        }

        // Extract table IDs from selected tables
        return selectedTables.stream()
            .map(table -> (String) table.get("id"))
            .collect(Collectors.toList());
    }

    /**
     * Helper method to safely extract table capacity from a table data map.
     *
     * @param table Map containing table data
     * @return The table capacity, or 0 if not available
     */
    private int getTableCapacity(Map<String, Object> table) {
        Object capacityObj = table.get("capacity");

        if (capacityObj == null) {
            return 0;
        }

        if (capacityObj instanceof Integer) {
            return (Integer) capacityObj;
        } else if (capacityObj instanceof Number) {
            return ((Number) capacityObj).intValue();
        } else {
            return 0;
        }
    }
}