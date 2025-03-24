package com.restaurant.restaurant.config;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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

@Configuration
public class DataInitializer {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

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
    
    private List<Restaurant> createSampleRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        
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
        restaurant1.setLocation(geometryFactory.createPoint(new Coordinate(-74.0060, 40.7128)));
        restaurant1.setActive(true);
        
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
        restaurant2.setLocation(geometryFactory.createPoint(new Coordinate(-122.4194, 37.7749)));
        restaurant2.setActive(true);
        
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
        restaurant3.setLocation(geometryFactory.createPoint(new Coordinate(-87.6298, 41.8781)));
        restaurant3.setActive(true);
        
        restaurants.add(restaurant1);
        restaurants.add(restaurant2);
        restaurants.add(restaurant3);
        
        return restaurants;
    }
    
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