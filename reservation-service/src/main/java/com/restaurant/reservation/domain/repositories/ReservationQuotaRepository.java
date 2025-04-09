package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.ReservationQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing ReservationQuota entities.
 * Provides methods for querying and managing reservation quotas for specific
 * restaurants, dates, and time slots.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface ReservationQuotaRepository extends JpaRepository<ReservationQuota, String> {

    /**
     * Finds all reservation quotas for a specific restaurant on a given date.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @return List of reservation quotas for the specified restaurant and date
     */
    List<ReservationQuota> findByRestaurantIdAndDate(String restaurantId, LocalDate date);
    
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
    Optional<ReservationQuota> findByRestaurantIdAndDateAndTimeSlot(
            String restaurantId, LocalDate date, LocalTime timeSlot);
}