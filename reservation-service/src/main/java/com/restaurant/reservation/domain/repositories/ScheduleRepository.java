package com.restaurant.reservation.domain.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.reservation.domain.models.Schedule;

/**
 * Repository interface for managing Schedule entities.
 * Provides methods for querying and managing restaurant schedules,
 * including daily operating hours, capacity, and special schedules.
 *
 * This repository includes:
 * - Restaurant-specific schedule queries
 * - Date range filtering
 * - Specific date lookup
 * - Support for custom and special schedules
 *
 * @author Restaurant Reservation Team
 * @version 1.1
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    /**
     * Finds all schedules for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of schedules for the specified restaurant, ordered by date
     */
    @Query("SELECT s FROM Schedule s WHERE s.restaurantId = :restaurantId ORDER BY s.date")
    List<Schedule> findByRestaurantId(@Param("restaurantId") String restaurantId);

    /**
     * Finds all schedules for a specific restaurant within a date range.
     *
     * @param restaurantId The ID of the restaurant
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return List of schedules within the specified date range, ordered by date
     */
    @Query("SELECT s FROM Schedule s WHERE s.restaurantId = :restaurantId AND " +
           "s.date >= :startDate AND s.date <= :endDate ORDER BY s.date")
    List<Schedule> findByRestaurantIdAndDateBetween(
            @Param("restaurantId") String restaurantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Finds a specific schedule for a restaurant on a given date.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @return Optional containing the schedule if found
     */
    @Query("SELECT s FROM Schedule s WHERE s.restaurantId = :restaurantId AND s.date = :date")
    Optional<Schedule> findByRestaurantIdAndDate(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date);

    /**
     * Finds all schedules for a specific restaurant where the restaurant is closed.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of schedules where the restaurant is closed, ordered by date
     */
    @Query("SELECT s FROM Schedule s WHERE s.restaurantId = :restaurantId AND s.closed = true ORDER BY s.date")
    List<Schedule> findClosedSchedulesByRestaurantId(@Param("restaurantId") String restaurantId);

    /**
     * Finds all schedules for a specific restaurant with special hours description.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of schedules with special hours, ordered by date
     */
    @Query("SELECT s FROM Schedule s WHERE s.restaurantId = :restaurantId AND " +
           "s.specialHoursDescription IS NOT NULL AND s.specialHoursDescription <> '' ORDER BY s.date")
    List<Schedule> findSchedulesWithSpecialHours(@Param("restaurantId") String restaurantId);

    /**
     * Counts the number of schedules for a specific restaurant within a date range.
     *
     * @param restaurantId The ID of the restaurant
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return The count of schedules
     */
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.restaurantId = :restaurantId AND " +
           "s.date >= :startDate AND s.date <= :endDate")
    long countSchedulesInDateRange(
            @Param("restaurantId") String restaurantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
