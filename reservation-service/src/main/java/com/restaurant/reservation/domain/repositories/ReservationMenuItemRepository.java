package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.ReservationMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for reservation menu item operations.
 * Provides methods for querying and manipulating reservation menu items in the database.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Repository
public interface ReservationMenuItemRepository extends JpaRepository<ReservationMenuItem, String> {
    
    /**
     * Finds all menu items for a specific reservation.
     * 
     * @param reservationId The ID of the reservation
     * @return List of menu items for the reservation
     */
    List<ReservationMenuItem> findByReservationId(String reservationId);
    
    /**
     * Finds a specific menu item in a reservation.
     * 
     * @param reservationId The ID of the reservation
     * @param menuItemId The ID of the menu item
     * @return The reservation menu item if found, null otherwise
     */
    ReservationMenuItem findByReservationIdAndMenuItemId(String reservationId, String menuItemId);
    
    /**
     * Deletes all menu items for a specific reservation.
     * 
     * @param reservationId The ID of the reservation
     */
    void deleteByReservationId(String reservationId);
}
