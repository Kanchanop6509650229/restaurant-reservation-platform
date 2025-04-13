package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for menu item operations.
 * Provides methods for querying and manipulating menu items in the database.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {
    
    /**
     * Finds all active menu items for a specific restaurant.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of active menu items for the restaurant
     */
    List<MenuItem> findByRestaurantIdAndActiveTrue(String restaurantId);
    
    /**
     * Finds all active and available menu items for a specific restaurant.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of active and available menu items for the restaurant
     */
    List<MenuItem> findByRestaurantIdAndActiveTrueAndAvailableTrue(String restaurantId);
    
    /**
     * Finds all active menu items for a specific restaurant and category.
     * 
     * @param restaurantId The ID of the restaurant
     * @param categoryId The ID of the category
     * @return List of active menu items for the restaurant and category
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.category.id = :categoryId AND m.active = true")
    List<MenuItem> findByRestaurantIdAndCategoryIdAndActiveTrue(
            @Param("restaurantId") String restaurantId, 
            @Param("categoryId") String categoryId);
    
    /**
     * Finds all active and available menu items for a specific restaurant and category.
     * 
     * @param restaurantId The ID of the restaurant
     * @param categoryId The ID of the category
     * @return List of active and available menu items for the restaurant and category
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.category.id = :categoryId AND m.active = true AND m.available = true")
    List<MenuItem> findByRestaurantIdAndCategoryIdAndActiveTrueAndAvailableTrue(
            @Param("restaurantId") String restaurantId, 
            @Param("categoryId") String categoryId);
    
    /**
     * Searches for active menu items by name or description for a specific restaurant.
     * 
     * @param restaurantId The ID of the restaurant
     * @param searchTerm The search term to match against name or description
     * @return List of matching menu items
     */
    @Query("SELECT m FROM MenuItem m WHERE m.restaurantId = :restaurantId AND m.active = true AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<MenuItem> searchByNameOrDescription(
            @Param("restaurantId") String restaurantId, 
            @Param("searchTerm") String searchTerm);
}
