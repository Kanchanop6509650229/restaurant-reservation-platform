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

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/public")
    public ResponseEntity<ResponseDTO<List<TableDTO>>> getAllTablesByRestaurantId(
            @PathVariable String restaurantId) {
        List<TableDTO> tables = tableService.getAllTablesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(tables));
    }

    @GetMapping("/public/available")
    public ResponseEntity<ResponseDTO<List<TableDTO>>> getAvailableTablesByRestaurantId(
            @PathVariable String restaurantId) {
        List<TableDTO> tables = tableService.getAvailableTablesByRestaurantId(restaurantId);
        return ResponseEntity.ok(ResponseDTO.success(tables));
    }

    @GetMapping("/public/{tableId}")
    public ResponseEntity<ResponseDTO<TableDTO>> getTableById(
            @PathVariable String restaurantId,
            @PathVariable String tableId) {
        TableDTO table = tableService.getTableById(tableId);
        return ResponseEntity.ok(ResponseDTO.success(table));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<TableDTO>> createTable(
            @PathVariable String restaurantId,
            @Valid @RequestBody TableCreateRequest createRequest) {
        TableDTO table = tableService.createTable(restaurantId, createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(table, "Table created successfully"));
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<ResponseDTO<TableDTO>> updateTable(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @Valid @RequestBody TableUpdateRequest updateRequest) {
        TableDTO table = tableService.updateTable(tableId, updateRequest);
        return ResponseEntity.ok(ResponseDTO.success(table, "Table updated successfully"));
    }

    @PatchMapping("/{tableId}/status")
    public ResponseEntity<ResponseDTO<TableDTO>> updateTableStatus(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @RequestParam String status,
            @RequestParam(required = false) String reservationId) {
        TableDTO table = tableService.updateTableStatus(tableId, status, reservationId);
        return ResponseEntity.ok(ResponseDTO.success(table, "Table status updated successfully"));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<ResponseDTO<Void>> deleteTable(
            @PathVariable String restaurantId,
            @PathVariable String tableId) {
        tableService.deleteTable(tableId);
        return ResponseEntity.ok(ResponseDTO.success(null, "Table deleted successfully"));
    }
}