package com.restaurant.reservation.domain.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.reservation.domain.models.ReservationQuota;

/**
 * Repository interface for managing ReservationQuota entities.
 * Provides methods for querying and managing reservation quotas for specific
 * restaurants, dates, and time slots.
 *
 * This repository includes:
 * - Date-specific quota queries
 * - Time slot filtering
 * - Time range queries
 * - Quota management operations
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Repository
public interface ReservationQuotaRepository extends JpaRepository<ReservationQuota, String> {

    /**
     * Finds all reservation quotas for a specific restaurant on a given date.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @return List of reservation quotas for the specified restaurant and date, ordered by time slot
     */
    @Query("SELECT q FROM ReservationQuota q WHERE q.restaurantId = :restaurantId AND q.date = :date ORDER BY q.timeSlot")
    List<ReservationQuota> findByRestaurantIdAndDate(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date);

    /**
     * Finds all reservation quotas for a specific restaurant on a given date
     * within a specified time range.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @param startTime The start time of the range (inclusive)
     * @param endTime The end time of the range (inclusive)
     * @return List of reservation quotas matching the criteria
     */
    @Query("SELECT q FROM ReservationQuota q WHERE q.restaurantId = :restaurantId AND " +
           "q.date = :date AND q.timeSlot >= :startTime AND q.timeSlot <= :endTime")
    List<ReservationQuota> findByRestaurantIdAndDateAndTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    /**
     * Finds a specific reservation quota for a restaurant on a given date and time slot.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @param timeSlot The specific time slot
     * @return Optional containing the reservation quota if found
     */
    @Query("SELECT q FROM ReservationQuota q WHERE q.restaurantId = :restaurantId AND " +
           "q.date = :date AND q.timeSlot = :timeSlot")
    Optional<ReservationQuota> findByRestaurantIdAndDateAndTimeSlot(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("timeSlot") LocalTime timeSlot);

    /**
     * Updates the current reservations and capacity for a specific time slot.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date of the quota
     * @param timeSlot The specific time slot
     * @param currentReservations The new current reservations value
     * @param currentCapacity The new current capacity value
     * @return The number of quotas updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE ReservationQuota q SET q.currentReservations = :currentReservations, " +
           "q.currentCapacity = :currentCapacity " +
           "WHERE q.restaurantId = :restaurantId AND q.date = :date AND q.timeSlot = :timeSlot")
    int updateCurrentQuota(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("timeSlot") LocalTime timeSlot,
            @Param("currentReservations") int currentReservations,
            @Param("currentCapacity") int currentCapacity);

    /**
     * Increments the current reservations and capacity for a specific time slot.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date of the quota
     * @param timeSlot The specific time slot
     * @param reservationIncrement The number of reservations to add
     * @param capacityIncrement The amount of capacity to add
     * @return The number of quotas updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE ReservationQuota q SET q.currentReservations = q.currentReservations + :reservationIncrement, " +
           "q.currentCapacity = q.currentCapacity + :capacityIncrement " +
           "WHERE q.restaurantId = :restaurantId AND q.date = :date AND q.timeSlot = :timeSlot " +
           "AND (q.currentReservations + :reservationIncrement) <= q.maxReservations " +
           "AND (q.currentCapacity + :capacityIncrement) <= q.maxCapacity")
    int incrementCurrentQuota(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("timeSlot") LocalTime timeSlot,
            @Param("reservationIncrement") int reservationIncrement,
            @Param("capacityIncrement") int capacityIncrement);

    /**
     * Decrements the current reservations and capacity for a specific time slot.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date of the quota
     * @param timeSlot The specific time slot
     * @param reservationDecrement The number of reservations to remove
     * @param capacityDecrement The amount of capacity to remove
     * @return The number of quotas updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE ReservationQuota q SET q.currentReservations = q.currentReservations - :reservationDecrement, " +
           "q.currentCapacity = q.currentCapacity - :capacityDecrement " +
           "WHERE q.restaurantId = :restaurantId AND q.date = :date AND q.timeSlot = :timeSlot " +
           "AND q.currentReservations >= :reservationDecrement " +
           "AND q.currentCapacity >= :capacityDecrement")
    int decrementCurrentQuota(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("timeSlot") LocalTime timeSlot,
            @Param("reservationDecrement") int reservationDecrement,
            @Param("capacityDecrement") int capacityDecrement);
}