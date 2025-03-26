package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.ReservationQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationQuotaRepository extends JpaRepository<ReservationQuota, String> {

    List<ReservationQuota> findByRestaurantIdAndDate(String restaurantId, LocalDate date);
    
    @Query("SELECT q FROM ReservationQuota q WHERE q.restaurantId = :restaurantId AND " +
           "q.date = :date AND q.timeSlot >= :startTime AND q.timeSlot <= :endTime")
    List<ReservationQuota> findByRestaurantIdAndDateAndTimeRange(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    Optional<ReservationQuota> findByRestaurantIdAndDateAndTimeSlot(
            String restaurantId, LocalDate date, LocalTime timeSlot);
}