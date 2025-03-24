package com.restaurant.restaurant.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.RestaurantTable;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, String> {

    List<RestaurantTable> findByRestaurantId(String restaurantId);
    
    List<RestaurantTable> findByRestaurantIdAndStatus(String restaurantId, String status);
    
    @Query("SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :restaurantId AND t.capacity >= :minCapacity AND t.capacity <= :maxCapacity")
    List<RestaurantTable> findTablesWithCapacityRange(@Param("restaurantId") String restaurantId, 
                                                    @Param("minCapacity") int minCapacity, 
                                                    @Param("maxCapacity") int maxCapacity);
    
    Optional<RestaurantTable> findByRestaurantIdAndTableNumber(String restaurantId, String tableNumber);
}