package com.restaurant.restaurant.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantTableRepository;
import com.restaurant.restaurant.dto.RestaurantStatisticsDTO;
import org.springframework.stereotype.Service;

/**
 * Service class for managing restaurant statistics.
 * This service provides functionality for:
 * - Retrieving comprehensive statistics about a restaurant's performance
 * - Analyzing table utilization and availability
 * - Monitoring restaurant ratings and capacity
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class RestaurantStatisticsService {

    /** Repository for restaurant data access */
    private final RestaurantRepository restaurantRepository;

    /** Repository for table data access */
    private final RestaurantTableRepository tableRepository;

    /**
     * Constructs a new RestaurantStatisticsService with required dependencies.
     *
     * @param restaurantRepository Repository for restaurant data access
     * @param tableRepository Repository for table data access
     */
    public RestaurantStatisticsService(RestaurantRepository restaurantRepository,
                                      RestaurantTableRepository tableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
    }

    /**
     * Retrieves comprehensive statistics for a specific restaurant.
     * The statistics include:
     * - Basic restaurant information (name, capacity)
     * - Rating information (average rating, total ratings)
     * - Table utilization (total, available, reserved, occupied)
     *
     * @param restaurantId The ID of the restaurant
     * @return RestaurantStatisticsDTO containing the statistics
     * @throws EntityNotFoundException if the restaurant is not found
     */
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