package com.restaurant.restaurant.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, String> {

    List<Branch> findByRestaurantId(String restaurantId);
    
    List<Branch> findByRestaurantIdAndActiveTrue(String restaurantId);
    
    @Query(value = "SELECT * FROM branches b WHERE active = true AND " +
                  "ST_DistanceSphere(location, ST_MakePoint(:longitude, :latitude)) <= :distance", 
           nativeQuery = true)
    List<Branch> findNearbyBranches(@Param("latitude") double latitude, 
                                   @Param("longitude") double longitude, 
                                   @Param("distance") double distanceInMeters);
}