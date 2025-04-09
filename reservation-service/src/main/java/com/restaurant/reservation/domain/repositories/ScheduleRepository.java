package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Schedule entities.
 * Provides methods for querying and managing restaurant schedules,
 * including daily operating hours, capacity, and special schedules.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    /**
     * Finds all schedules for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of schedules for the specified restaurant
     */
    List<Schedule> findByRestaurantId(String restaurantId);
    
    /**
     * Finds all schedules for a specific restaurant within a date range.
     *
     * @param restaurantId The ID of the restaurant
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return List of schedules within the specified date range
     */
    List<Schedule> findByRestaurantIdAndDateBetween(String restaurantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds a specific schedule for a restaurant on a given date.
     *
     * @param restaurantId The ID of the restaurant
     * @param date The date to search for
     * @return Optional containing the schedule if found
     */
    Optional<Schedule> findByRestaurantIdAndDate(String restaurantId, LocalDate date);
}
