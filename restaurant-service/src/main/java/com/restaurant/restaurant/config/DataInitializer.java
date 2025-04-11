package com.restaurant.restaurant.config;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.restaurant.domain.models.OperatingHours;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.models.RestaurantTable;
import com.restaurant.restaurant.domain.repositories.OperatingHoursRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.domain.repositories.RestaurantTableRepository;

/**
 * Configuration class for initializing sample data in the restaurant service.
 * This class provides sample data for development and testing purposes, including:
 * - Sample restaurants with different cuisines and locations
 * - Restaurant tables with varying capacities and locations
 * - Operating hours with different schedules based on restaurant type
 * 
 * The initialization is performed only when:
 * - The application is not running in test profile
 * - The restaurant repository is empty
 * 
 * Sample data includes:
 * - Three restaurants: Italian Bistro, Sushi Paradise, and Spice of India
 * - Customized tables and operating hours for each restaurant type
 * - Realistic business hours with breaks and special schedules
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Configuration
public class DataInitializer {

    // Removing the GeometryFactory to avoid issues with spatial data

    /**
     * Creates a CommandLineRunner bean that initializes sample data.
     * This method is only active when not running in test profile.
     * It creates sample restaurants, tables, and operating hours if the database is empty.
     *
     * @param restaurantRepository Repository for restaurant entities
     * @param tableRepository Repository for restaurant table entities
     * @param operatingHoursRepository Repository for operating hours entities
     * @return CommandLineRunner that executes the initialization
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initData(RestaurantRepository restaurantRepository,
                                     RestaurantTableRepository tableRepository,
                                     OperatingHoursRepository operatingHoursRepository) {
        return args -> {
            // Only seed data if the repository is empty
            if (restaurantRepository.count() == 0) {
                // Create sample restaurants
                List<Restaurant> restaurants = createSampleRestaurants();
                restaurants = restaurantRepository.saveAll(restaurants);
                
                // Create tables for each restaurant
                for (Restaurant restaurant : restaurants) {
                    List<RestaurantTable> tables = createSampleTables(restaurant);
                    tableRepository.saveAll(tables);
                    
                    // Create operating hours for each restaurant
                    List<OperatingHours> hours = createSampleOperatingHours(restaurant);
                    operatingHoursRepository.saveAll(hours);
                }
            }
        };
    }
    
    /**
     * Creates sample restaurant entities with different cuisines and locations.
     * Each restaurant has:
     * - Unique name and description
     * - Complete address information
     * - Contact details
     * - Capacity and location coordinates
     * - Active status and owner ID
     *
     * @return List of sample restaurant entities
     */
    private List<Restaurant> createSampleRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        String defaultOwnerId = "admin";
        
        // Restaurant 1
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Italian Bistro");
        restaurant1.setDescription("Authentic Italian cuisine in a cozy atmosphere");
        restaurant1.setAddress("123 Main St");
        restaurant1.setCity("New York");
        restaurant1.setState("NY");
        restaurant1.setZipCode("10001");
        restaurant1.setCountry("USA");
        restaurant1.setPhoneNumber("212-555-1234");
        restaurant1.setEmail("info@italianbistro.com");
        restaurant1.setWebsite("https://www.italianbistro.com");
        restaurant1.setCuisineType("Italian");
        restaurant1.setTotalCapacity(60);
        restaurant1.setLatitude(40.7128);
        restaurant1.setLongitude(-74.0060);
        restaurant1.setActive(true);
        restaurant1.setOwnerId(defaultOwnerId);
        
        // Restaurant 2
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Sushi Paradise");
        restaurant2.setDescription("Fresh and delicious Japanese sushi");
        restaurant2.setAddress("456 Oak St");
        restaurant2.setCity("San Francisco");
        restaurant2.setState("CA");
        restaurant2.setZipCode("94101");
        restaurant2.setCountry("USA");
        restaurant2.setPhoneNumber("415-555-6789");
        restaurant2.setEmail("info@sushiparadise.com");
        restaurant2.setWebsite("https://www.sushiparadise.com");
        restaurant2.setCuisineType("Japanese");
        restaurant2.setTotalCapacity(45);
        restaurant2.setLatitude(37.7749);
        restaurant2.setLongitude(-122.4194);
        restaurant2.setActive(true);
        restaurant2.setOwnerId(defaultOwnerId);
        
        // Restaurant 3
        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Spice of India");
        restaurant3.setDescription("Traditional Indian cuisine with modern twists");
        restaurant3.setAddress("789 Pine St");
        restaurant3.setCity("Chicago");
        restaurant3.setState("IL");
        restaurant3.setZipCode("60601");
        restaurant3.setCountry("USA");
        restaurant3.setPhoneNumber("312-555-9012");
        restaurant3.setEmail("info@spiceofindia.com");
        restaurant3.setWebsite("https://www.spiceofindia.com");
        restaurant3.setCuisineType("Indian");
        restaurant3.setTotalCapacity(75);
        restaurant3.setLatitude(41.8781);
        restaurant3.setLongitude(-87.6298);
        restaurant3.setActive(true);
        restaurant3.setOwnerId(defaultOwnerId);
        
        restaurants.add(restaurant1);
        restaurants.add(restaurant2);
        restaurants.add(restaurant3);
        
        return restaurants;
    }
    
    /**
     * Creates sample tables for a given restaurant.
     * The number and configuration of tables varies based on the restaurant's cuisine type:
     * - Italian: 6 tables with window, center, private, and outdoor locations
     * - Japanese: 5 tables with tatami, sushi bar, and private locations
     * - Indian: 7 tables with window, center, outdoor, and private locations
     * - Others: 3 generic tables
     *
     * @param restaurant The restaurant to create tables for
     * @return List of sample table entities
     */
    private List<RestaurantTable> createSampleTables(Restaurant restaurant) {
        List<RestaurantTable> tables = new ArrayList<>();
        
        // Create tables based on restaurant type
        if (restaurant.getCuisineType().equals("Italian")) {
            // 6 tables of different sizes for Italian restaurant
            tables.add(new RestaurantTable(restaurant, "1", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "2", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "3", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "4", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "5", 6, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "6", 8, StatusCodes.TABLE_AVAILABLE));
            
            // Set additional properties
            tables.get(0).setLocation("WINDOW");
            tables.get(1).setLocation("WINDOW");
            tables.get(2).setLocation("CENTER");
            tables.get(3).setLocation("CENTER");
            tables.get(4).setLocation("PRIVATE");
            tables.get(5).setLocation("OUTDOOR");
            
        } else if (restaurant.getCuisineType().equals("Japanese")) {
            // 5 tables for Japanese restaurant
            tables.add(new RestaurantTable(restaurant, "1", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "2", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "3", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "4", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "5", 8, StatusCodes.TABLE_AVAILABLE));
            
            // Set additional properties
            tables.get(0).setLocation("TATAMI");
            tables.get(1).setLocation("TATAMI");
            tables.get(2).setLocation("TATAMI");
            tables.get(3).setLocation("SUSHI_BAR");
            tables.get(4).setLocation("PRIVATE");
            
        } else if (restaurant.getCuisineType().equals("Indian")) {
            // 7 tables for Indian restaurant
            tables.add(new RestaurantTable(restaurant, "1", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "2", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "3", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "4", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "5", 6, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "6", 6, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "7", 10, StatusCodes.TABLE_AVAILABLE));
            
            // Set additional properties
            tables.get(0).setLocation("WINDOW");
            tables.get(1).setLocation("WINDOW");
            tables.get(2).setLocation("CENTER");
            tables.get(3).setLocation("CENTER");
            tables.get(4).setLocation("OUTDOOR");
            tables.get(5).setLocation("OUTDOOR");
            tables.get(6).setLocation("PRIVATE");
        } else {
            // Generic tables for other restaurant types
            tables.add(new RestaurantTable(restaurant, "1", 2, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "2", 4, StatusCodes.TABLE_AVAILABLE));
            tables.add(new RestaurantTable(restaurant, "3", 6, StatusCodes.TABLE_AVAILABLE));
        }
        
        return tables;
    }
    
    /**
     * Creates operating hours for a given restaurant.
     * Operating hours vary based on the restaurant's cuisine type:
     * - Italian: 11:30-22:00, closed on Mondays
     * - Japanese: 12:00-22:30, closed on Tuesdays, with lunch break 15:00-17:00
     * - Indian: 12:00-23:00, with special Sunday hours 13:00-22:00
     * - Others: Default hours 11:00-22:00
     *
     * @param restaurant The restaurant to create operating hours for
     * @return List of operating hours for each day of the week
     */
    private List<OperatingHours> createSampleOperatingHours(Restaurant restaurant) {
        List<OperatingHours> hours = new ArrayList<>();
        
        // Default operating hours for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            OperatingHours operatingHours = new OperatingHours();
            operatingHours.setRestaurant(restaurant);
            operatingHours.setDayOfWeek(day);
            
            // Different hours based on restaurant type
            if (restaurant.getCuisineType().equals("Italian")) {
                operatingHours.setOpenTime(LocalTime.of(11, 30));
                operatingHours.setCloseTime(LocalTime.of(22, 0));
                // Closed on Mondays
                if (day == DayOfWeek.MONDAY) {
                    operatingHours.setClosed(true);
                }
            } else if (restaurant.getCuisineType().equals("Japanese")) {
                operatingHours.setOpenTime(LocalTime.of(12, 0));
                operatingHours.setCloseTime(LocalTime.of(22, 30));
                // Closed on Tuesdays
                if (day == DayOfWeek.TUESDAY) {
                    operatingHours.setClosed(true);
                }
                // Lunch break
                if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                    operatingHours.setBreakStartTime(LocalTime.of(15, 0));
                    operatingHours.setBreakEndTime(LocalTime.of(17, 0));
                }
            } else if (restaurant.getCuisineType().equals("Indian")) {
                operatingHours.setOpenTime(LocalTime.of(12, 0));
                operatingHours.setCloseTime(LocalTime.of(23, 0));
                // Open late on Sundays
                if (day == DayOfWeek.SUNDAY) {
                    operatingHours.setOpenTime(LocalTime.of(13, 0));
                    operatingHours.setCloseTime(LocalTime.of(22, 0));
                }
            } else {
                // Default hours
                operatingHours.setOpenTime(LocalTime.of(11, 0));
                operatingHours.setCloseTime(LocalTime.of(22, 0));
            }
            
            hours.add(operatingHours);
        }
        
        return hours;
    }
}