package com.restaurant.reservation.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.reservation.domain.models.Reservation;
import com.restaurant.reservation.domain.models.ReservationHistory;

/**
 * Repository interface for managing ReservationHistory entities.
 * Provides methods for querying and managing reservation history records,
 * including filtering by reservation, action type, and time periods.
 * 
 * This repository includes:
 * - Reservation-specific history queries
 * - Action-based filtering
 * - Time-based history filtering
 * - Pagination support for history records
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, String> {

    /**
     * Finds all history records for a specific reservation.
     *
     * @param reservation The reservation entity
     * @return List of history records for the specified reservation, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.reservation = :reservation ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByReservation(@Param("reservation") Reservation reservation);
    
    /**
     * Finds all history records for a specific reservation with pagination support.
     *
     * @param reservation The reservation entity
     * @param pageable Pagination information
     * @return Page of history records for the specified reservation
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.reservation = :reservation ORDER BY h.timestamp DESC")
    Page<ReservationHistory> findByReservation(@Param("reservation") Reservation reservation, Pageable pageable);
    
    /**
     * Finds all history records for a specific reservation ID.
     *
     * @param reservationId The ID of the reservation
     * @return List of history records for the specified reservation, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.reservation.id = :reservationId ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByReservationId(@Param("reservationId") String reservationId);
    
    /**
     * Finds all history records for a specific action type.
     *
     * @param action The action type (e.g., "CREATED", "UPDATED", "CANCELLED")
     * @return List of history records for the specified action, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.action = :action ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByAction(@Param("action") String action);
    
    /**
     * Finds all history records created within a specific time range.
     *
     * @param startTime The start time of the range (inclusive)
     * @param endTime The end time of the range (inclusive)
     * @return List of history records within the specified time range, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.timestamp >= :startTime AND h.timestamp <= :endTime ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByTimestampBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * Finds all history records for a specific user who performed the actions.
     *
     * @param performedBy The ID of the user who performed the actions
     * @return List of history records for the specified user, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.performedBy = :performedBy ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByPerformedBy(@Param("performedBy") String performedBy);
    
    /**
     * Finds all history records for a specific reservation and action type.
     *
     * @param reservation The reservation entity
     * @param action The action type (e.g., "CREATED", "UPDATED", "CANCELLED")
     * @return List of history records matching the criteria, ordered by timestamp descending
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.reservation = :reservation AND h.action = :action ORDER BY h.timestamp DESC")
    List<ReservationHistory> findByReservationAndAction(
            @Param("reservation") Reservation reservation,
            @Param("action") String action);
    
    /**
     * Counts the number of history records for a specific reservation.
     *
     * @param reservationId The ID of the reservation
     * @return The count of history records
     */
    @Query("SELECT COUNT(h) FROM ReservationHistory h WHERE h.reservation.id = :reservationId")
    long countByReservationId(@Param("reservationId") String reservationId);
    
    /**
     * Finds the most recent history record for a specific reservation.
     *
     * @param reservationId The ID of the reservation
     * @return The most recent history record, or null if none exists
     */
    @Query("SELECT h FROM ReservationHistory h WHERE h.reservation.id = :reservationId ORDER BY h.timestamp DESC")
    ReservationHistory findMostRecentByReservationId(@Param("reservationId") String reservationId);
}
