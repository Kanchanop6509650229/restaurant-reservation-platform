package com.restaurant.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.models.RestaurantTable;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantTableRepository;
import com.restaurant.restaurant.dto.TableCreateRequest;
import com.restaurant.restaurant.dto.TableDTO;
import com.restaurant.restaurant.dto.TableUpdateRequest;
import com.restaurant.restaurant.exception.TableStatusException;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

import jakarta.transaction.Transactional;

/**
 * Service class for managing restaurant table operations.
 * This service provides functionality for:
 * - Managing restaurant tables (CRUD operations)
 * - Updating table status and availability
 * - Validating table configurations
 * - Managing table capacity and features
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class TableService {

    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(TableService.class);

    /** Repository for table data access */
    private final RestaurantTableRepository tableRepository;

    /** Repository for restaurant data access */
    private final RestaurantRepository restaurantRepository;

    /** Producer for restaurant-related events */
    private final RestaurantEventProducer restaurantEventProducer;

    /**
     * Constructs a new TableService with required dependencies.
     *
     * @param tableRepository Repository for table data access
     * @param restaurantRepository Repository for restaurant data access
     * @param restaurantEventProducer Producer for restaurant-related events
     */
    public TableService(RestaurantTableRepository tableRepository,
            RestaurantRepository restaurantRepository,
            RestaurantEventProducer restaurantEventProducer) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }

    /**
     * Retrieves all tables for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of TableDTOs for the restaurant
     * @throws EntityNotFoundException if the restaurant is not found
     * @throws ValidationException if the restaurant is inactive
     */
    public List<TableDTO> getAllTablesByRestaurantId(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot get tables for an inactive restaurant");
        }

        return tableRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all available tables for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of available TableDTOs
     * @throws EntityNotFoundException if the restaurant is not found
     * @throws ValidationException if the restaurant is inactive
     */
    public List<TableDTO> getAvailableTablesByRestaurantId(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot get available tables for an inactive restaurant");
        }

        return tableRepository.findByRestaurantIdAndStatus(restaurantId, StatusCodes.TABLE_AVAILABLE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific table by its ID.
     *
     * @param id The ID of the table to retrieve
     * @return TableDTO for the requested table
     * @throws EntityNotFoundException if the table is not found
     * @throws ValidationException if the restaurant is inactive
     */
    public TableDTO getTableById(String id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));

        if (!table.getRestaurant().isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot get table for an inactive restaurant");
        }

        return convertToDTO(table);
    }

    /**
     * Creates a new table for a restaurant.
     * This method:
     * - Validates the creation request
     * - Creates the table entity
     * - Updates restaurant capacity
     *
     * @param restaurantId The ID of the restaurant
     * @param createRequest The table creation request
     * @return Created TableDTO
     * @throws EntityNotFoundException if the restaurant is not found
     * @throws ValidationException if the restaurant is inactive or validation fails
     */
    @Transactional
    public TableDTO createTable(String restaurantId, TableCreateRequest createRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot create table for an inactive restaurant");
        }

        validateTableRequest(createRequest, restaurantId);

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setTableNumber(createRequest.getTableNumber());
        table.setCapacity(createRequest.getCapacity());
        table.setStatus(StatusCodes.TABLE_AVAILABLE);
        table.setLocation(createRequest.getLocation());
        table.setAccessible(createRequest.isAccessible());
        table.setShape(createRequest.getShape());
        table.setMinCapacity(createRequest.getMinCapacity());
        table.setCombinable(createRequest.isCombinable());
        table.setSpecialFeatures(createRequest.getSpecialFeatures());

        RestaurantTable savedTable = tableRepository.save(table);

        // Update restaurant total capacity
        updateRestaurantCapacity(restaurant);

        return convertToDTO(savedTable);
    }

    /**
     * Updates an existing table.
     * This method:
     * - Validates the update request
     * - Updates table fields
     * - Updates restaurant capacity if needed
     *
     * @param id The ID of the table to update
     * @param updateRequest The update request
     * @return Updated TableDTO
     * @throws EntityNotFoundException if the table is not found
     * @throws ValidationException if the restaurant is inactive
     */
    @Transactional
    public TableDTO updateTable(String id, TableUpdateRequest updateRequest) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));

        if (!table.getRestaurant().isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update table for an inactive restaurant");
        }

        if (updateRequest.getTableNumber() != null) {
            table.setTableNumber(updateRequest.getTableNumber());
        }

        if (updateRequest.getCapacity() > 0) {
            table.setCapacity(updateRequest.getCapacity());
        }

        if (updateRequest.getLocation() != null) {
            table.setLocation(updateRequest.getLocation());
        }

        table.setAccessible(updateRequest.isAccessible());

        if (updateRequest.getShape() != null) {
            table.setShape(updateRequest.getShape());
        }

        if (updateRequest.getMinCapacity() > 0) {
            table.setMinCapacity(updateRequest.getMinCapacity());
        }

        table.setCombinable(updateRequest.isCombinable());

        if (updateRequest.getSpecialFeatures() != null) {
            table.setSpecialFeatures(updateRequest.getSpecialFeatures());
        }

        RestaurantTable updatedTable = tableRepository.save(table);

        // Update restaurant total capacity if capacity changed
        updateRestaurantCapacity(updatedTable.getRestaurant());

        return convertToDTO(updatedTable);
    }

    /**
     * Updates the status of a table.
     * This method:
     * - Validates the status transition
     * - Updates the table status
     * - Publishes a status change event
     *
     * @param id The ID of the table
     * @param status The new status
     * @param reservationId The ID of the reservation (optional)
     * @return Updated TableDTO
     * @throws EntityNotFoundException if the table is not found
     * @throws ValidationException if the restaurant is inactive or validation fails
     */
    @Transactional
    public TableDTO updateTableStatus(String id, String status, String reservationId) {
        return updateTableStatusInternal(id, status, reservationId, true);
    }

    /**
     * Updates table status without publishing an event.
     * This method is used by Kafka consumers to avoid event loops.
     *
     * @param id The ID of the table
     * @param status The new status
     * @param reservationId The ID of the reservation (optional)
     * @return Updated TableDTO
     * @throws EntityNotFoundException if the table is not found
     * @throws ValidationException if the restaurant is inactive or validation fails
     */
    @Transactional
    public TableDTO updateTableStatusWithoutEvent(String id, String status, String reservationId) {
        logger.info("Updating table status without event: tableId={}, newStatus={}, reservationId={}",
                id, status, reservationId);

        try {
            RestaurantTable table = tableRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Table not found: {}", id);
                        return new EntityNotFoundException("Table", id);
                    });

            String oldStatus = table.getStatus();
            logger.info("Found table: {} with current status: {}", id, oldStatus);

            // If status is the same, no need to update
            if (oldStatus.equals(status)) {
                logger.info("Table status unchanged, skipping update: {}", id);
                return convertToDTO(table);
            }

            // Set the new status
            table.setStatus(status);

            // Save and flush to ensure the transaction is committed
            RestaurantTable updatedTable = tableRepository.saveAndFlush(table);
            logger.info("Successfully updated table status: {} from {} to {}",
                    id, oldStatus, status);

            return convertToDTO(updatedTable);
        } catch (Exception e) {
            logger.error("Failed to update table status: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Internal method for updating table status.
     * This method handles the core status update logic and event publishing.
     *
     * @param id The ID of the table
     * @param status The new status
     * @param reservationId The ID of the reservation (optional)
     * @param publishEvent Whether to publish a status change event
     * @return Updated TableDTO
     */
    private TableDTO updateTableStatusInternal(String id, String status, String reservationId, boolean publishEvent) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));

        if (!table.getRestaurant().isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update the status of a table in an inactive restaurant");
        }

        isValidTableStatus(status);
        String oldStatus = table.getStatus();

        // Don't update if status is the same
        if (oldStatus.equals(status)) {
            return convertToDTO(table);
        }

        // Specific validation for status transitions
        validateStatusTransition(table, status);

        table.setStatus(status);

        RestaurantTable updatedTable = tableRepository.save(table);

        // Publish table status changed event
        if (publishEvent) {
            try {
                restaurantEventProducer.publishTableStatusChangedEvent(
                        new TableStatusChangedEvent(
                                updatedTable.getRestaurant().getId(),
                                updatedTable.getId(),
                                oldStatus,
                                status,
                                reservationId));
            } catch (Exception e) {
                logger.error("Failed to publish table status change event: {}", e.getMessage(), e);
                // Continue with the update even if event publishing fails
            }
        }

        return convertToDTO(updatedTable);
    }

    /**
     * Validates a table status transition.
     * This method ensures that status changes follow valid transitions.
     *
     * @param table The table to validate
     * @param newStatus The new status to validate
     * @throws TableStatusException if the transition is invalid
     */
    private void validateStatusTransition(RestaurantTable table, String newStatus) {
        String currentStatus = table.getStatus();
        String tableNumber = table.getTableNumber();

        // Example validation logic - customize based on business rules
        if (currentStatus.equals(StatusCodes.TABLE_MAINTENANCE) &&
                !newStatus.equals(StatusCodes.TABLE_AVAILABLE)) {
            throw TableStatusException.invalidStatusTransition(tableNumber, currentStatus, newStatus);
        }

        if (currentStatus.equals(StatusCodes.TABLE_RESERVED) &&
                newStatus.equals(StatusCodes.TABLE_MAINTENANCE)) {
            throw new ValidationException("status",
                    String.format(
                            "Cannot set table %s to maintenance while it has active reservations. Cancel the reservations first.",
                            tableNumber));
        }
    }

    /**
     * Deletes a table.
     * This method:
     * - Removes the table
     * - Updates restaurant capacity
     *
     * @param id The ID of the table to delete
     * @throws EntityNotFoundException if the table is not found
     */
    @Transactional
    public void deleteTable(String id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));

        if (!table.getRestaurant().isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot delete table from an inactive restaurant");
        }

        Restaurant restaurant = table.getRestaurant();

        // Actually delete the table (not just marking inactive)
        tableRepository.delete(table);

        // Update restaurant total capacity
        updateRestaurantCapacity(restaurant);
    }

    /**
     * Validates a table creation request.
     * This method checks:
     * - Required fields are present
     * - Table number is unique
     * - Capacity values are valid
     *
     * @param request The creation request to validate
     * @param restaurantId The ID of the restaurant
     * @throws ValidationException if validation fails
     */
    private void validateTableRequest(TableCreateRequest request, String restaurantId) {
        Map<String, String> errors = new HashMap<>();

        if (request.getTableNumber() == null || request.getTableNumber().trim().isEmpty()) {
            errors.put("tableNumber", "Table number is required to identify the table in the restaurant");
        } else if (tableRepository.findByRestaurantIdAndTableNumber(restaurantId, request.getTableNumber())
                .isPresent()) {
            errors.put("tableNumber",
                    String.format("Table with number %s already exists in this restaurant", request.getTableNumber()));
        }

        if (request.getCapacity() <= 0) {
            errors.put("capacity", "Table capacity must be at least 1 person");
        } else if (request.getCapacity() > 20) {
            errors.put("capacity",
                    "Table capacity cannot exceed 20 people. For larger parties, consider multiple tables");
        }

        if (request.getMinCapacity() > request.getCapacity()) {
            errors.put("minCapacity", "Minimum capacity cannot be greater than the maximum capacity");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Table validation failed", errors);
        }
    }

    /**
     * Checks if a table status is valid.
     *
     * @param status The status to validate
     * @return true if the status is valid
     * @throws ValidationException if the status is invalid
     */
    private boolean isValidTableStatus(String status) {
        boolean isValid = status.equals(StatusCodes.TABLE_AVAILABLE) ||
                status.equals(StatusCodes.TABLE_OCCUPIED) ||
                status.equals(StatusCodes.TABLE_RESERVED) ||
                status.equals(StatusCodes.TABLE_MAINTENANCE);

        if (!isValid) {
            throw new ValidationException("status",
                    String.format("Invalid table status: '%s'. Valid statuses are: '%s', '%s', '%s', '%s'",
                            status,
                            StatusCodes.TABLE_AVAILABLE,
                            StatusCodes.TABLE_OCCUPIED,
                            StatusCodes.TABLE_RESERVED,
                            StatusCodes.TABLE_MAINTENANCE));
        }

        return true;
    }

    /**
     * Updates the total capacity of a restaurant.
     * This method recalculates the total capacity based on all tables.
     *
     * @param restaurant The restaurant to update
     */
    private void updateRestaurantCapacity(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update capacity for an inactive restaurant");
        }

        List<RestaurantTable> activeTables = tableRepository.findByRestaurantIdAndStatus(
                restaurant.getId(), StatusCodes.TABLE_AVAILABLE);

        int totalCapacity = activeTables.stream()
                .mapToInt(RestaurantTable::getCapacity)
                .sum();

        int oldCapacity = restaurant.getTotalCapacity();

        if (oldCapacity != totalCapacity) {
            restaurant.setTotalCapacity(totalCapacity);
            restaurantRepository.save(restaurant);

            // Publish capacity changed event
            restaurantEventProducer.publishCapacityChangedEvent(
                    restaurant.getId(), oldCapacity, totalCapacity, "Table Update");
        }
    }

    /**
     * Converts a RestaurantTable entity to its DTO representation.
     *
     * @param table The RestaurantTable entity to convert
     * @return TableDTO representation of the entity
     */
    private TableDTO convertToDTO(RestaurantTable table) {
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setRestaurantId(table.getRestaurant().getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setCapacity(table.getCapacity());
        dto.setStatus(table.getStatus());
        dto.setLocation(table.getLocation());
        dto.setAccessible(table.isAccessible());
        dto.setShape(table.getShape());
        dto.setMinCapacity(table.getMinCapacity());
        dto.setCombinable(table.isCombinable());
        dto.setSpecialFeatures(table.getSpecialFeatures());
        return dto;
    }
}