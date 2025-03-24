package com.restaurant.restaurant.service;

import com.restaurant.common.constants.StatusCodes;
import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.dto.RestaurantCreateRequest;
import com.restaurant.restaurant.dto.RestaurantSearchCriteria;
import com.restaurant.restaurant.dto.RestaurantUpdateRequest;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OperatingHoursService operatingHoursService;
    private final RestaurantEventProducer restaurantEventProducer;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public RestaurantService(RestaurantRepository restaurantRepository, 
                             OperatingHoursService operatingHoursService,
                             RestaurantEventProducer restaurantEventProducer) {
        this.restaurantRepository = restaurantRepository;
        this.operatingHoursService = operatingHoursService;
        this.restaurantEventProducer = restaurantEventProducer;
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<RestaurantDTO> getAllRestaurantsPaged(Pageable pageable) {
        return restaurantRepository.findByActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    public RestaurantDTO getRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
        return convertToDTO(restaurant);
    }

    public Page<RestaurantDTO> searchRestaurants(RestaurantSearchCriteria criteria, Pageable pageable) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            return restaurantRepository.searchRestaurants(criteria.getKeyword(), pageable)
                    .map(this::convertToDTO);
        } else {
            // Default search
            return getAllRestaurantsPaged(pageable);
        }
    }

    public List<RestaurantDTO> findNearbyRestaurants(double latitude, double longitude, double distanceInKm) {
        // Convert km to meters
        double distanceInMeters = distanceInKm * 1000;
        
        return restaurantRepository.findNearbyRestaurants(latitude, longitude, distanceInMeters).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantDTO createRestaurant(RestaurantCreateRequest createRequest) {
        validateRestaurantRequest(createRequest);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(createRequest.getName());
        restaurant.setDescription(createRequest.getDescription());
        restaurant.setAddress(createRequest.getAddress());
        restaurant.setCity(createRequest.getCity());
        restaurant.setState(createRequest.getState());
        restaurant.setZipCode(createRequest.getZipCode());
        restaurant.setCountry(createRequest.getCountry());
        restaurant.setPhoneNumber(createRequest.getPhoneNumber());
        restaurant.setEmail(createRequest.getEmail());
        restaurant.setWebsite(createRequest.getWebsite());
        restaurant.setCuisineType(createRequest.getCuisineType());
        restaurant.setTotalCapacity(createRequest.getTotalCapacity());
        restaurant.setActive(true);
        
        // Set coordinates if provided
        if (createRequest.getLatitude() != 0 && createRequest.getLongitude() != 0) {
            restaurant.setLatitude(createRequest.getLatitude());
            restaurant.setLongitude(createRequest.getLongitude());
            
            // Create Point geometry for spatial queries
            Point point = geometryFactory.createPoint(
                    new Coordinate(createRequest.getLongitude(), createRequest.getLatitude()));
            restaurant.setLocation(point);
        }

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        // Create default operating hours
        operatingHoursService.createDefaultOperatingHours(savedRestaurant);

        return convertToDTO(savedRestaurant);
    }

    @Transactional
    public RestaurantDTO updateRestaurant(String id, RestaurantUpdateRequest updateRequest) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));

        // Track changes for event publishing
        if (updateRequest.getName() != null && !updateRequest.getName().equals(restaurant.getName())) {
            String oldValue = restaurant.getName();
            restaurant.setName(updateRequest.getName());
            
            // Publish restaurant updated event
            restaurantEventProducer.publishRestaurantUpdatedEvent(
                    new RestaurantUpdatedEvent(id, "name", oldValue, updateRequest.getName()));
        }

        if (updateRequest.getDescription() != null) {
            restaurant.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getAddress() != null) {
            restaurant.setAddress(updateRequest.getAddress());
        }

        if (updateRequest.getCity() != null) {
            restaurant.setCity(updateRequest.getCity());
        }

        if (updateRequest.getState() != null) {
            restaurant.setState(updateRequest.getState());
        }

        if (updateRequest.getZipCode() != null) {
            restaurant.setZipCode(updateRequest.getZipCode());
        }

        if (updateRequest.getCountry() != null) {
            restaurant.setCountry(updateRequest.getCountry());
        }

        if (updateRequest.getPhoneNumber() != null) {
            restaurant.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        if (updateRequest.getEmail() != null) {
            restaurant.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getWebsite() != null) {
            restaurant.setWebsite(updateRequest.getWebsite());
        }

        if (updateRequest.getCuisineType() != null) {
            restaurant.setCuisineType(updateRequest.getCuisineType());
        }

        if (updateRequest.getTotalCapacity() != 0 && 
            updateRequest.getTotalCapacity() != restaurant.getTotalCapacity()) {
            int oldCapacity = restaurant.getTotalCapacity();
            restaurant.setTotalCapacity(updateRequest.getTotalCapacity());
            
            // Publish capacity changed event
            restaurantEventProducer.publishCapacityChangedEvent(
                    id, oldCapacity, updateRequest.getTotalCapacity(), "Manual Update");
        }

        // Update geolocation if provided
        if (updateRequest.getLatitude() != 0 && updateRequest.getLongitude() != 0) {
            restaurant.setLatitude(updateRequest.getLatitude());
            restaurant.setLongitude(updateRequest.getLongitude());
            
            // Update Point geometry
            Point point = geometryFactory.createPoint(
                    new Coordinate(updateRequest.getLongitude(), updateRequest.getLatitude()));
            restaurant.setLocation(point);
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return convertToDTO(updatedRestaurant);
    }

    @Transactional
    public void toggleRestaurantActive(String id, boolean active) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
        
        if (restaurant.isActive() != active) {
            restaurant.setActive(active);
            restaurantRepository.save(restaurant);
            
            // Publish restaurant updated event
            restaurantEventProducer.publishRestaurantUpdatedEvent(
                    new RestaurantUpdatedEvent(id, "active", 
                    String.valueOf(!active), String.valueOf(active)));
        }
    }

    @Transactional
    public void deleteRestaurant(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
        
        // Instead of deleting, mark as inactive
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
        
        // Publish restaurant updated event
        restaurantEventProducer.publishRestaurantUpdatedEvent(
                new RestaurantUpdatedEvent(id, "active", "true", "false"));
    }

    private void validateRestaurantRequest(RestaurantCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("name", "Restaurant name is required");
        }
        
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            throw new ValidationException("address", "Restaurant address is required");
        }
        
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new ValidationException("phoneNumber", "Restaurant phone number is required");
        }
        
        if (request.getCuisineType() == null || request.getCuisineType().trim().isEmpty()) {
            throw new ValidationException("cuisineType", "Restaurant cuisine type is required");
        }
    }

    public RestaurantDTO convertToDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setEmail(restaurant.getEmail());
        dto.setWebsite(restaurant.getWebsite());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setCapacity(restaurant.getTotalCapacity());
        dto.setAverageRating(restaurant.getAverageRating());
        dto.setActive(restaurant.isActive());
        
        // We'll set operating hours in a separate service call if needed
        
        return dto;
    }
}