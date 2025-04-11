package com.restaurant.restaurant.domain.repositories;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.OperatingHours;

/**
 * Repository interface for managing OperatingHours entities.
 * This interface provides:
 * - CRUD operations through JpaRepository
 * - Custom queries for finding operating hours by restaurant
 * - Day-specific operating hours lookup
 * 
 * The repository is responsible for data access operations
 * related to restaurant operating hours and schedules.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, String> {

    /**
     * Finds all operating hours associated with a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of operating hours for the restaurant
     */
    List<OperatingHours> findByRestaurantId(String restaurantId);
    
    /**
     * Finds operating hours for a specific day of the week at a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param dayOfWeek The day of the week to find hours for
     * @return Optional containing the operating hours if found
     */
    Optional<OperatingHours> findByRestaurantIdAndDayOfWeek(String restaurantId, DayOfWeek dayOfWeek);
}