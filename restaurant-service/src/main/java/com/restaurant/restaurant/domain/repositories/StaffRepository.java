package com.restaurant.restaurant.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    List<Staff> findByRestaurantId(String restaurantId);
    
    List<Staff> findByRestaurantIdAndPosition(String restaurantId, String position);
    
    Optional<Staff> findByUserId(String userId);
}