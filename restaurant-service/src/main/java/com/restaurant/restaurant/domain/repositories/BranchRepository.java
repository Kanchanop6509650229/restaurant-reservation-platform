package com.restaurant.restaurant.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Branch;

/**
 * Repository interface for managing Branch entities.
 * This interface provides:
 * - CRUD operations through JpaRepository
 * - Custom queries for finding branches by restaurant
 * - Geographic search capabilities for nearby branches
 * 
 * The repository is responsible for data access operations
 * related to restaurant branches, including location-based queries.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, String> {

    /**
     * Finds all branches associated with a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of branches belonging to the restaurant
     */
    List<Branch> findByRestaurantId(String restaurantId);
    
    /**
     * Finds all active branches associated with a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of active branches belonging to the restaurant
     */
    List<Branch> findByRestaurantIdAndActiveTrue(String restaurantId);
    
    /**
     * Finds branches within a specified distance from given coordinates.
     * This method uses PostGIS spatial functions to calculate distances
     * between geographic points.
     *
     * @param latitude The latitude coordinate of the search center
     * @param longitude The longitude coordinate of the search center
     * @param distanceInMeters The maximum distance in meters from the search center
     * @return List of branches within the specified distance
     */
    @Query(value = "SELECT * FROM branches b WHERE active = true AND " +
                  "ST_DistanceSphere(location, ST_MakePoint(:longitude, :latitude)) <= :distance", 
           nativeQuery = true)
    List<Branch> findNearbyBranches(@Param("latitude") double latitude, 
                                   @Param("longitude") double longitude, 
                                   @Param("distance") double distanceInMeters);
}