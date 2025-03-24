package com.restaurant.restaurant.service;

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
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final RestaurantTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventProducer restaurantEventProducer;

    public TableService(RestaurantTableRepository tableRepository, 
                        RestaurantRepository restaurantRepository,
                        RestaurantEventProducer restaurantEventProducer) {
        this.tableRepository = tableRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantEventProducer = restaurantEventProducer;
    }

    public List<TableDTO> getAllTablesByRestaurantId(String restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TableDTO> getAvailableTablesByRestaurantId(String restaurantId) {
        return tableRepository.findByRestaurantIdAndStatus(restaurantId, StatusCodes.TABLE_AVAILABLE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TableDTO getTableById(String id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));
        return convertToDTO(table);
    }

    @Transactional
    public TableDTO createTable(String restaurantId, TableCreateRequest createRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        validateTableRequest(createRequest);

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

    @Transactional
    public TableDTO updateTable(String id, TableUpdateRequest updateRequest) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));

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

    @Transactional
    public TableDTO updateTableStatus(String id, String status, String reservationId) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));
        
        if (!isValidTableStatus(status)) {
            throw new ValidationException("status", "Invalid table status: " + status);
        }
        
        String oldStatus = table.getStatus();
        
        // Don't update if status is the same
        if (oldStatus.equals(status)) {
            return convertToDTO(table);
        }
        
        table.setStatus(status);
        
        RestaurantTable updatedTable = tableRepository.save(table);
        
        // Publish table status changed event
        restaurantEventProducer.publishTableStatusChangedEvent(
                new TableStatusChangedEvent(
                        updatedTable.getRestaurant().getId(),
                        updatedTable.getId(),
                        oldStatus,
                        status,
                        reservationId
                ));
        
        return convertToDTO(updatedTable);
    }

    @Transactional
    public void deleteTable(String id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table", id));
        
        Restaurant restaurant = table.getRestaurant();
        
        // Actually delete the table (not just marking inactive)
        tableRepository.delete(table);
        
        // Update restaurant total capacity
        updateRestaurantCapacity(restaurant);
    }

    private void validateTableRequest(TableCreateRequest request) {
        if (request.getTableNumber() == null || request.getTableNumber().trim().isEmpty()) {
            throw new ValidationException("tableNumber", "Table number is required");
        }
        
        if (request.getCapacity() <= 0) {
            throw new ValidationException("capacity", "Table capacity must be greater than 0");
        }
    }

    private boolean isValidTableStatus(String status) {
        return status.equals(StatusCodes.TABLE_AVAILABLE) ||
               status.equals(StatusCodes.TABLE_OCCUPIED) ||
               status.equals(StatusCodes.TABLE_RESERVED) ||
               status.equals(StatusCodes.TABLE_MAINTENANCE);
    }

    private void updateRestaurantCapacity(Restaurant restaurant) {
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