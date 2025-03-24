package com.restaurant.restaurant.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantTableRepository;
import com.restaurant.restaurant.dto.RestaurantStatisticsDTO;
import org.springframework.stereotype.Service;

@Service
public class RestaurantStatisticsService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository tableRepository;

    public RestaurantStatisticsService(RestaurantRepository restaurantRepository,
                                      RestaurantTableRepository tableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
    }

    public RestaurantStatisticsDTO getRestaurantStatistics(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));
        
        RestaurantStatisticsDTO statistics = new RestaurantStatisticsDTO(restaurantId, restaurant.getName());
        
        // Set basic statistics
        statistics.setTotalCapacity(restaurant.getTotalCapacity());
        statistics.setAverageRating(restaurant.getAverageRating());
        statistics.setTotalRatings(restaurant.getTotalRatings());
        
        // Get table counts
        var allTables = tableRepository.findByRestaurantId(restaurantId);
        statistics.setTotalTables(allTables.size());
        
        // Count tables by status
        long availableTables = allTables.stream()
                .filter(t -> t.getStatus().equals(StatusCodes.TABLE_AVAILABLE))
                .count();
        statistics.setAvailableTables((int) availableTables);
        
        long reservedTables = allTables.stream()
                .filter(t -> t.getStatus().equals(StatusCodes.TABLE_RESERVED))
                .count();
        statistics.setReservedTables((int) reservedTables);
        
        long occupiedTables = allTables.stream()
                .filter(t -> t.getStatus().equals(StatusCodes.TABLE_OCCUPIED))
                .count();
        statistics.setOccupiedTables((int) occupiedTables);
        
        return statistics;
    }
}