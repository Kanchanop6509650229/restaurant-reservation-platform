package com.restaurant.restaurant.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.Staff;

/**
 * Repository interface for managing Staff entities.
 * This interface provides:
 * - CRUD operations through JpaRepository
 * - Custom queries for finding staff by restaurant
 * - Position-based staff lookup
 * - User account association
 * 
 * The repository is responsible for data access operations
 * related to restaurant staff members and their roles.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

    /**
     * Finds all staff members associated with a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return List of staff members working at the restaurant
     */
    List<Staff> findByRestaurantId(String restaurantId);
    
    /**
     * Finds staff members with a specific position at a restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @param position The position to search for
     * @return List of staff members with the specified position
     */
    List<Staff> findByRestaurantIdAndPosition(String restaurantId, String position);
    
    /**
     * Finds a staff member by their associated user account ID.
     *
     * @param userId The ID of the user account
     * @return Optional containing the staff member if found
     */
    Optional<Staff> findByUserId(String userId);
}