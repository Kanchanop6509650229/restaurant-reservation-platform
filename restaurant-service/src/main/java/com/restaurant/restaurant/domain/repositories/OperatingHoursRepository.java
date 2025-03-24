package com.restaurant.restaurant.domain.repositories;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.restaurant.domain.models.OperatingHours;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, String> {

    List<OperatingHours> findByRestaurantId(String restaurantId);
    
    Optional<OperatingHours> findByRestaurantIdAndDayOfWeek(String restaurantId, DayOfWeek dayOfWeek);
}