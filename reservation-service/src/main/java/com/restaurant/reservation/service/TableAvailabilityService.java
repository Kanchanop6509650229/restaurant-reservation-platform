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

@Service
public class TableAvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityService.class);

    private final ReservationRepository reservationRepository;
    private final ReservationEventProducer eventProducer;
    private final RestTemplate restTemplate;
    private final TableStatusCacheService tableStatusCacheService;
    private final TableResponseManager tableResponseManager;
    
    @Value("${table.availability.request.timeout:10}")
    private long requestTimeoutSeconds;
    
    @Value("${restaurant-service.url:http://localhost:8082}")
    private String restaurantServiceUrl;

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

    private String getTableStatus(String tableId) {
        // First check the cache
        String cachedStatus = tableStatusCacheService.getTableStatus(tableId);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        
        // If not in cache, default to available
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
    
    // เอาไว้ใช้เป็น fallback ในกรณีที่ Kafka ไม่ตอบกลับ
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