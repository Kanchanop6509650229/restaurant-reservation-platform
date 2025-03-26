package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByUserId(String userId);
    
    Page<Reservation> findByUserId(String userId, Pageable pageable);
    
    List<Reservation> findByRestaurantId(String restaurantId);
    
    Page<Reservation> findByRestaurantId(String restaurantId, Pageable pageable);
    
    List<Reservation> findByRestaurantIdAndStatus(String restaurantId, String status);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND " +
           "r.reservationTime >= :startTime AND r.reservationTime <= :endTime")
    List<Reservation> findByRestaurantIdAndTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND " +
           "r.tableId = :tableId AND r.status IN ('CONFIRMED', 'PENDING') AND " +
           "((r.reservationTime <= :endTime AND " +
           "r.reservationTime >= :startTime) OR " +
           "(FUNCTION('DATEADD', MINUTE, r.durationMinutes, r.reservationTime) > :startTime AND " +
           "r.reservationTime < :startTime))")
    List<Reservation> findConflictingReservations(
            @Param("restaurantId") String restaurantId,
            @Param("tableId") String tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND " +
           "r.confirmationDeadline < :now")
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND " +
           "r.reservationTime < :pastTime")
    List<Reservation> findUncompletedPastReservations(@Param("pastTime") LocalDateTime pastTime);
}