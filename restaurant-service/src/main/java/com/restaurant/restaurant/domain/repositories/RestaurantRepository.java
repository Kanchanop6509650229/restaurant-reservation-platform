package com.restaurant.restaurant.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    List<Restaurant> findByActiveTrue();
    
    Page<Restaurant> findByActiveTrue(Pageable pageable);
    
    List<Restaurant> findByCuisineTypeAndActiveTrue(String cuisineType);
    
    @Query("SELECT r FROM Restaurant r WHERE r.active = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.city) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Restaurant> searchRestaurants(@Param("keyword") String keyword, Pageable pageable);
    
    @Query(value = "SELECT * FROM restaurants r WHERE active = true AND " +
                  "ST_DistanceSphere(location, ST_MakePoint(:longitude, :latitude)) <= :distance", 
           nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(@Param("latitude") double latitude, 
                                          @Param("longitude") double longitude, 
                                          @Param("distance") double distanceInMeters);
}