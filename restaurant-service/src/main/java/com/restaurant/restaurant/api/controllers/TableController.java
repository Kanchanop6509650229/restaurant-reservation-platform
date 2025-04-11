package com.restaurant.restaurant.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.restaurant.dto.TableCreateRequest;
import com.restaurant.restaurant.dto.TableDTO;
import com.restaurant.restaurant.dto.TableUpdateRequest;
import com.restaurant.restaurant.service.TableService;

import jakarta.validation.Valid;

/**
 * REST Controller for managing restaurant table operations.
 * This controller provides endpoints for:
 * - Managing restaurant tables (CRUD operations)
 * - Checking table availability
 * - Updating table status
 * 
 * All endpoints are prefixed with '/api/restaurants/{restaurantId}/tables'.
 * Public endpoints are further prefixed with '/public'.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/tables")
public class TableController {

    /** Service layer for table operations */
    private final TableService tableService;

    /**
     * Constructs a new TableController with required dependencies.
     *
     * @param tableService Service layer for table operations
     */
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * Retrieves all tables for a specific restaurant.
     * This endpoint is publicly accessible.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing a list of TableDTOs
     */
    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<List<TableDTO>>> getAllTablesByRestaurantId(
            @PathVariable String restaurantId) {
        List<TableDTO> tables = tableService.getAllTablesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(tables));
    }

    /**
     * Retrieves all available tables for a specific restaurant.
     * This endpoint is publicly accessible.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing a list of available TableDTOs
     */
    @GetMapping("/public/available")
    public ResponseEntity<ResponseDTO<List<TableDTO>>> getAvailableTablesByRestaurantId(
            @PathVariable String restaurantId) {
        List<TableDTO> tables = tableService.getAvailableTablesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(tables));
    }

    /**
     * Retrieves a specific table by its ID.
     * This endpoint is publicly accessible.
     *
     * @param restaurantId The ID of the restaurant
     * @param tableId The ID of the table to retrieve
     * @return ResponseEntity containing the requested TableDTO
     */
    @GetMapping("/public/{tableId}")
    public ResponseEntity<ResponseDTO<TableDTO>> getTableById(
            @PathVariable String restaurantId,
            @PathVariable String tableId) {
        TableDTO table = tableService.getTableById(tableId);
        return ResponseEntity.ok(ResponseDTO.success(table));
    }

    /**
     * Creates a new table for a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param createRequest The table creation request
     * @return ResponseEntity containing the created TableDTO
     */
    @PostMapping
    public ResponseEntity<ResponseDTO<TableDTO>> createTable(
            @PathVariable String restaurantId,
            @Valid @RequestBody TableCreateRequest createRequest) {
        TableDTO table = tableService.createTable(restaurantId, createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(table, "Table created successfully"));
    }

    /**
     * Updates an existing table.
     *
     * @param restaurantId The ID of the restaurant
     * @param tableId The ID of the table to update
     * @param updateRequest The table update request
     * @return ResponseEntity containing the updated TableDTO
     */
    @PutMapping("/{tableId}")
    public ResponseEntity<ResponseDTO<TableDTO>> updateTable(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @Valid @RequestBody TableUpdateRequest updateRequest) {
        TableDTO table = tableService.updateTable(tableId, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(table, "Table updated successfully"));
    }

    /**
     * Updates the status of a table.
     * This endpoint is used to mark tables as occupied, available, or reserved.
     *
     * @param restaurantId The ID of the restaurant
     * @param tableId The ID of the table to update
     * @param status The new status of the table
     * @param reservationId The ID of the reservation (optional)
     * @return ResponseEntity containing the updated TableDTO
     */
    @PatchMapping("/{tableId}/status")
    public ResponseEntity<ResponseDTO<TableDTO>> updateTableStatus(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @RequestParam String status,
            @RequestParam(required = false) String reservationId) {
        TableDTO table = tableService.updateTableStatus(tableId, status, reservationId);
        return ResponseEntity.ok(ResponseDTO.success(table, "Table status updated successfully"));
    }

    /**
     * Deletes a table.
     *
     * @param restaurantId The ID of the restaurant
     * @param tableId The ID of the table to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{tableId}")
    public ResponseEntity<ResponseDTO<Void>> deleteTable(
            @PathVariable String restaurantId,
            @PathVariable String tableId) {
        tableService.deleteTable(tableId);
        return ResponseEntity.ok(ResponseDTO.success(null, "Table deleted successfully"));
    }
}