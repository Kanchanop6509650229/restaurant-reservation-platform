package com.restaurant.restaurant.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.RestaurantTable;

/**
 * Repository interface for managing RestaurantTable entities.
 * This interface provides:
 * - CRUD operations through JpaRepository
 * - Custom queries for finding tables by restaurant
 * - Status-based table filtering
 * - Capacity-based table search
 * - Table number lookup
 * 
 * The repository is responsible for data access operations
 * related to restaurant tables and their configurations.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, String> {

    /**
     * Finds all tables associated with a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of tables belonging to the restaurant
     */
    List<RestaurantTable> findByRestaurantId(String restaurantId);
    
    /**
     * Finds tables with a specific status at a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param status The status to filter by
     * @return List of tables with the specified status
     */
    List<RestaurantTable> findByRestaurantIdAndStatus(String restaurantId, String status);
    
    /**
     * Finds tables within a specific capacity range at a restaurant.
     * This method is useful for finding tables that can accommodate
     * a certain number of guests.
     *
     * @param restaurantId The ID of the restaurant
     * @param minCapacity The minimum required capacity
     * @param maxCapacity The maximum allowed capacity
     * @return List of tables within the specified capacity range
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :restaurantId AND t.capacity >= :minCapacity AND t.capacity <= :maxCapacity")
    List<RestaurantTable> findTablesWithCapacityRange(@Param("restaurantId") String restaurantId, 
                                                    @Param("minCapacity") int minCapacity, 
                                                    @Param("maxCapacity") int maxCapacity);
    
    /**
     * Finds a specific table by its number at a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param tableNumber The unique table number
     * @return Optional containing the table if found
     */
    Optional<RestaurantTable> findByRestaurantIdAndTableNumber(String restaurantId, String tableNumber);
}