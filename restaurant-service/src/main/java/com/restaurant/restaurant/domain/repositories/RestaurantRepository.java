package com.restaurant.restaurant.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Restaurant;

/**
 * Repository interface for managing Restaurant entities.
 * This interface provides:
 * - CRUD operations through JpaRepository
 * - Custom queries for finding active restaurants
 * - Cuisine-based restaurant search
 * - Full-text search across multiple fields
 * - Geographic search capabilities
 * 
 * The repository is responsible for data access operations
 * related to restaurants, including advanced search and filtering.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    /**
     * Finds all active restaurants in the system.
     *
     * @return List of active restaurants
     */
    List<Restaurant> findByActiveTrue();
    
    /**
     * Finds all active restaurants with pagination support.
     *
     * @param pageable Pagination information
     * @return Page of active restaurants
     */
    Page<Restaurant> findByActiveTrue(Pageable pageable);
    
    /**
     * Finds active restaurants by cuisine type.
     *
     * @param cuisineType The type of cuisine to search for
     * @return List of active restaurants serving the specified cuisine
     */
    List<Restaurant> findByCuisineTypeAndActiveTrue(String cuisineType);
    
    /**
     * Searches restaurants using a keyword across multiple fields.
     * The search is case-insensitive and matches against:
     * - Restaurant name
     * - Cuisine type
     * - City
     *
     * @param keyword The search term
     * @param pageable Pagination information
     * @return Page of matching restaurants
     */
    @Query("SELECT r FROM Restaurant r WHERE r.active = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Restaurant> searchRestaurants(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Finds restaurants within a specified distance from given coordinates.
     * This method uses PostGIS spatial functions to calculate distances
     * between geographic points.
     *
     * @param latitude The latitude coordinate of the search center
     * @param longitude The longitude coordinate of the search center
     * @param distanceInMeters The maximum distance in meters from the search center
     * @return List of restaurants within the specified distance
     */
    @Query(value = "SELECT * FROM restaurants r WHERE active = true AND " +
                  "ST_DistanceSphere(location, ST_MakePoint(:longitude, :latitude)) <= :distance", 
           nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(@Param("latitude") double latitude, 
                                          @Param("longitude") double longitude, 
                                          @Param("distance") double distanceInMeters);
}