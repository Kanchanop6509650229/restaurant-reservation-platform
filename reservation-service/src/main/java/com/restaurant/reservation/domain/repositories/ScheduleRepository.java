package com.restaurant.reservation.domain.repositories;

import com.restaurant.reservation.domain.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    List<Schedule> findByRestaurantId(String restaurantId);
    
    List<Schedule> findByRestaurantIdAndDateBetween(String restaurantId, LocalDate startDate, LocalDate endDate);
    
    Optional<Schedule> findByRestaurantIdAndDate(String restaurantId, LocalDate date);
}
