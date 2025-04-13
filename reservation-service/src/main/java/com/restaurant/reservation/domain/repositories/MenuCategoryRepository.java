package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for menu category operations.
 * Provides methods for querying and manipulating menu categories in the database.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, String> {
    
    /**
     * Finds all active menu categories for a specific restaurant, ordered by display order.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of active menu categories for the restaurant
     */
    List<MenuCategory> findByRestaurantIdAndActiveTrueOrderByDisplayOrderAsc(String restaurantId);
    
    /**
     * Finds all menu categories for a specific restaurant, ordered by display order.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of all menu categories for the restaurant
     */
    List<MenuCategory> findByRestaurantIdOrderByDisplayOrderAsc(String restaurantId);
}
