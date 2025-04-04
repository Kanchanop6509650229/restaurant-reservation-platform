package com.restaurant.reservation.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.events.restaurant.TableStatusChangedEvent;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.reservation.domain.models.Reservation;
import com.restaurant.reservation.domain.repositories.ReservationRepository;
import com.restaurant.reservation.kafka.producers.ReservationEventProducer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TableAvailabilityService {

    private final ReservationRepository reservationRepository;
    private final ReservationEventProducer eventProducer;
    private final RestTemplate restTemplate;
    private final TableStatusCacheService tableStatusCacheService;
    
    @Value("${restaurant-service.url:http://localhost:8082}")
    private String restaurantServiceUrl;

    public TableAvailabilityService(ReservationRepository reservationRepository,
                                   ReservationEventProducer eventProducer,
                                   RestTemplate restTemplate,
                                   TableStatusCacheService tableStatusCacheService) {
        this.reservationRepository = reservationRepository;
        this.eventProducer = eventProducer;
        this.restTemplate = restTemplate;
        this.tableStatusCacheService = tableStatusCacheService;
    }

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
        
        // Find suitable table for this reservation
        String tableId = findSuitableTable(
                reservation.getRestaurantId(),
                reservation.getReservationTime(),
                reservation.getEndTime(),
                reservation.getPartySize());
        
        if (tableId == null) {
            // No table available, keep as is
            return;
        }
        
        // Assign table to reservation
        reservation.setTableId(tableId);
        reservationRepository.save(reservation);
        
        // Publish table status changed event via Kafka instead of REST call
        publishTableStatusEvent(
            tableId, 
            reservation.getRestaurantId(),
            getTableStatus(tableId), 
            StatusCodes.TABLE_RESERVED, 
            reservation.getId()
        );
    }

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
    }

    private String findSuitableTable(String restaurantId, LocalDateTime startTime, 
                                    LocalDateTime endTime, int partySize) {
        try {
            // Still use REST API to get available tables initially
            // This could be improved in the future with a caching mechanism or pub/sub model
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
            // Log the error and continue
        }
        
        return null;
    }

    private String getTableStatus(String tableId) {
        // First check the cache
        String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        
        // If not in cache, default to available
        // In a more comprehensive solution, we might want to query the restaurant service
        return StatusCodes.TABLE_AVAILABLE;
    }
    
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
}