package com.restaurant.reservation.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.reservation.domain.models.Reservation;

/**
 * Repository interface for managing Reservation entities.
 * Provides methods for querying and managing restaurant reservations,
 * including pagination support and complex queries for reservation management.
 *
 * This repository includes:
 * - User-specific reservation queries
 * - Restaurant-specific reservation queries
 * - Time-based reservation filtering
 * - Conflict detection for overlapping reservations
 * - Status-based reservation management
 * - Automated reservation lifecycle management
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    /**
     * Finds all reservations for a specific user.
     *
     * @param userId The ID of the user
     * @return List of reservations for the specified user, ordered by reservation time descending
     */
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.reservationTime DESC")
    List<Reservation> findByUserId(@Param("userId") String userId);

    /**
     * Finds all reservations for a specific user with pagination support.
     *
     * @param userId The ID of the user
     * @param pageable Pagination information
     * @return Page of reservations for the specified user
     */
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.reservationTime DESC")
    Page<Reservation> findByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Finds all reservations for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of reservations for the specified restaurant, ordered by reservation time descending
     */
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId ORDER BY r.reservationTime DESC")
    List<Reservation> findByRestaurantId(@Param("restaurantId") String restaurantId);

    /**
     * Finds all reservations for a specific restaurant with pagination support.
     *
     * @param restaurantId The ID of the restaurant
     * @param pageable Pagination information
     * @return Page of reservations for the specified restaurant
     */
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId ORDER BY r.reservationTime DESC")
    Page<Reservation> findByRestaurantId(@Param("restaurantId") String restaurantId, Pageable pageable);

    /**
     * Finds all reservations for a specific restaurant with a given status.
     *
     * @param restaurantId The ID of the restaurant
     * @param status The status of the reservations to find
     * @return List of reservations matching the criteria, ordered by reservation time descending
     */
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND r.status = :status ORDER BY r.reservationTime DESC")
    List<Reservation> findByRestaurantIdAndStatus(
            @Param("restaurantId") String restaurantId,
            @Param("status") String status);

    /**
     * Finds all reservations for a specific restaurant within a given time range.
     *
     * @param restaurantId The ID of the restaurant
     * @param startTime The start time of the range (inclusive)
     * @param endTime The end time of the range (inclusive)
     * @return List of reservations within the specified time range
     */
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND " +
           "r.reservationTime >= :startTime AND r.reservationTime <= :endTime")
    List<Reservation> findByRestaurantIdAndTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Finds all reservations that conflict with a given time slot for a specific table.
     * A conflict occurs when:
     * 1. The reservation time falls within the specified range, or
     * 2. The reservation's end time (reservationTime + durationMinutes) overlaps with the range
     *
     * @param restaurantId The ID of the restaurant
     * @param tableId The ID of the table
     * @param startTime The start time of the range to check
     * @param endTime The end time of the range to check
     * @return List of conflicting reservations
     */
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND " +
           "r.tableId = :tableId AND r.status IN ('CONFIRMED', 'PENDING') AND " +
           "((r.reservationTime <= :endTime AND " +
           "r.reservationTime >= :startTime) OR " +
           "(FUNCTION('DATEADD', MINUTE, r.durationMinutes, r.reservationTime) > :startTime AND " +
           "r.reservationTime < :startTime))")
    List<Reservation> findConflictingReservations(
            @Param("restaurantId") String restaurantId,
            @Param("tableId") String tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Finds all pending reservations that have passed their confirmation deadline.
     * These reservations should be automatically cancelled.
     *
     * @param now The current time
     * @return List of expired pending reservations
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND " +
           "r.confirmationDeadline < :now")
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);

    /**
     * Updates the status of expired pending reservations to CANCELLED.
     * This method is used for batch processing of expired reservations.
     *
     * @param now The current time
     * @param newStatus The new status to set (typically 'CANCELLED')
     * @return The number of reservations updated
     */
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :newStatus, r.updatedAt = :now " +
           "WHERE r.status = 'PENDING' AND r.confirmationDeadline < :now")
    int updateExpiredPendingReservations(
            @Param("now") LocalDateTime now,
            @Param("newStatus") String newStatus);

    /**
     * Finds all confirmed reservations that are in the past but not yet marked as completed.
     * These reservations should be automatically marked as completed.
     *
     * @param pastTime The time to compare against (typically current time)
     * @return List of uncompleted past reservations
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND " +
           "r.reservationTime < :pastTime")
    List<Reservation> findUncompletedPastReservations(@Param("pastTime") LocalDateTime pastTime);

    /**
     * Updates the status of past confirmed reservations to COMPLETED.
     * This method is used for batch processing of completed reservations.
     *
     * @param pastTime The time to compare against (typically current time)
     * @param newStatus The new status to set (typically 'COMPLETED')
     * @return The number of reservations updated
     */
    @Modifying
    @Query("UPDATE Reservation r SET r.status = :newStatus, r.updatedAt = :now " +
           "WHERE r.status = 'CONFIRMED' AND r.reservationTime < :pastTime")
    int updateUncompletedPastReservations(
            @Param("pastTime") LocalDateTime pastTime,
            @Param("now") LocalDateTime now,
            @Param("newStatus") String newStatus);

    /**
     * Counts the number of reservations for a specific restaurant on a given date.
     *
     * @param restaurantId The ID of the restaurant
     * @param startOfDay The start of the day
     * @param endOfDay The end of the day
     * @return The count of reservations
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurantId = :restaurantId AND " +
           "r.reservationTime >= :startOfDay AND r.reservationTime < :endOfDay AND " +
           "r.status IN ('CONFIRMED', 'PENDING')")
    long countReservationsForDate(
            @Param("restaurantId") String restaurantId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}