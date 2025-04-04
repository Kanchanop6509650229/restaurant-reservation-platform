package com.restaurant.restaurant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.restaurant.common.dto.restaurant.RestaurantDTO;
import com.restaurant.common.events.restaurant.RestaurantUpdatedEvent;
import com.restaurant.common.exceptions.BaseException;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.restaurant.domain.models.Restaurant;
import com.restaurant.restaurant.domain.repositories.RestaurantRepository;
import com.restaurant.restaurant.dto.RestaurantCreateRequest;
import com.restaurant.restaurant.dto.RestaurantSearchCriteria;
import com.restaurant.restaurant.dto.RestaurantUpdateRequest;
import com.restaurant.restaurant.kafka.producers.RestaurantEventProducer;

import jakarta.transaction.Transactional;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
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

        if (!restaurant.isActive()) {
            throw new ValidationException("ไม่สามารถดึงข้อมูลร้านอาหารที่ถูกลบไปแล้ว");
        }

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
        restaurant.setOwnerId(createRequest.getOwnerId());

        // Set coordinates if provided
        if (createRequest.getLatitude() != 0 && createRequest.getLongitude() != 0) {
            restaurant.setLatitude(createRequest.getLatitude());
            restaurant.setLongitude(createRequest.getLongitude());

            // Create Point geometry for spatial queries
            // Point point = geometryFactory.createPoint(
            // new Coordinate(createRequest.getLongitude(), createRequest.getLatitude()));
            // restaurant.setLocation(point);
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

        if (!restaurant.isActive()) {
            throw new ValidationException("restaurant",
                    "Cannot update an inactive restaurant. Please activate the restaurant first.");
        }

        Map<String, String> validationErrors = new HashMap<>();

        try {
            // Validate name if provided
            if (updateRequest.getName() != null) {
                if (updateRequest.getName().trim().isEmpty()) {
                    validationErrors.put("name", "Restaurant name cannot be empty");
                } else if (updateRequest.getName().length() > 255) {
                    validationErrors.put("name", "Restaurant name cannot exceed 255 characters");
                } else {
                    String oldValue = restaurant.getName();
                    restaurant.setName(updateRequest.getName());

                    // Publish restaurant updated event
                    try {
                        restaurantEventProducer.publishRestaurantUpdatedEvent(
                                new RestaurantUpdatedEvent(id, "name", oldValue, updateRequest.getName()));
                    } catch (Exception e) {
                        logger.error("Error publishing name update event: {}", e.getMessage());
                    }
                }
            }

            // Update description if provided
            if (updateRequest.getDescription() != null) {
                restaurant.setDescription(updateRequest.getDescription());
            }

            // Update address if provided
            if (updateRequest.getAddress() != null) {
                if (updateRequest.getAddress().length() > 255) {
                    validationErrors.put("address", "Address cannot exceed 255 characters");
                } else {
                    restaurant.setAddress(updateRequest.getAddress());
                }
            }

            // Update city, state, zipCode, country if provided
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

            // Validate and update phone number if provided
            if (updateRequest.getPhoneNumber() != null) {
                if (!updateRequest.getPhoneNumber().matches("^\\+?[0-9\\s-()]{8,20}$")) {
                    validationErrors.put("phoneNumber", "Please provide a valid phone number format");
                } else {
                    restaurant.setPhoneNumber(updateRequest.getPhoneNumber());
                }
            }

            // Validate and update email if provided
            if (updateRequest.getEmail() != null) {
                String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
                if (!pattern.matcher(updateRequest.getEmail()).matches()) {
                    validationErrors.put("email", "Please provide a valid email address");
                } else {
                    restaurant.setEmail(updateRequest.getEmail());
                }
            }

            // Validate and update website if provided
            if (updateRequest.getWebsite() != null) {
                String urlRegex = "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(?:/[\\w\\-\\./?%&=]*)?$";
                if (!updateRequest.getWebsite().matches(urlRegex)) {
                    validationErrors.put("website",
                            "Please provide a valid website URL starting with http:// or https://");
                } else {
                    restaurant.setWebsite(updateRequest.getWebsite());
                }
            }

            // Update cuisine type if provided
            if (updateRequest.getCuisineType() != null) {
                restaurant.setCuisineType(updateRequest.getCuisineType());
            }

            // Update total capacity if provided
            if (updateRequest.getTotalCapacity() != 0 &&
                    updateRequest.getTotalCapacity() != restaurant.getTotalCapacity()) {
                int oldCapacity = restaurant.getTotalCapacity();
                restaurant.setTotalCapacity(updateRequest.getTotalCapacity());

                // Publish capacity changed event
                try {
                    restaurantEventProducer.publishCapacityChangedEvent(
                            id, oldCapacity, updateRequest.getTotalCapacity(), "Manual Update");
                } catch (Exception e) {
                    logger.error("Error publishing capacity update event: {}", e.getMessage());
                }
            }

            // Update geolocation if provided
            if (updateRequest.getLatitude() != 0 && updateRequest.getLongitude() != 0) {
                // Validate coordinates
                if (updateRequest.getLatitude() < -90 || updateRequest.getLatitude() > 90 ||
                        updateRequest.getLongitude() < -180 || updateRequest.getLongitude() > 180) {
                    validationErrors.put("coordinates", "Please provide valid geographic coordinates");
                } else {
                    restaurant.setLatitude(updateRequest.getLatitude());
                    restaurant.setLongitude(updateRequest.getLongitude());
                }
            }

            if (!validationErrors.isEmpty()) {
                throw new ValidationException("Restaurant update validation failed", validationErrors);
            }

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            // Log the error but continue with the update
            logger.error("Error during restaurant update: {}", e.getMessage(), e);
            throw new BaseException("Error updating restaurant: " + e.getMessage(), "UPDATE_ERROR");
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return convertToDTO(updatedRestaurant);
    }

    @Transactional
    public void toggleRestaurantActive(String id, boolean active) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));

        if (!restaurant.isActive()) {
            throw new ValidationException("ไม่สามารถอัปเดตร้านอาหารที่ถูกลบไปแล้ว");
        }

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
        Map<String, String> errors = new HashMap<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.put("name", "Restaurant name is required");
        } else if (request.getName().length() < 2) {
            errors.put("name", "Restaurant name must be at least 2 characters long");
        } else if (request.getName().length() > 255) {
            errors.put("name", "Restaurant name cannot exceed 255 characters");
        }

        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            errors.put("address", "Restaurant address is required for location purposes");
        } else if (request.getAddress().length() > 255) {
            errors.put("address", "Address cannot exceed 255 characters");
        }

        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            errors.put("phoneNumber", "Restaurant phone number is required for customer inquiries");
        } else {
            // Simple phone number format validation
            if (!request.getPhoneNumber().matches("^\\+?[0-9\\s-()]{8,20}$")) {
                errors.put("phoneNumber", "Please provide a valid phone number format");
            }
        }

        if (request.getCuisineType() == null || request.getCuisineType().trim().isEmpty()) {
            errors.put("cuisineType", "Cuisine type is required to categorize the restaurant");
        }

        if (request.getOwnerId() == null || request.getOwnerId().trim().isEmpty()) {
            errors.put("ownerId", "Restaurant owner ID is required for authorization purposes");
        }

        // Validate email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
            Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
            if (!pattern.matcher(request.getEmail()).matches()) {
                errors.put("email", "Please provide a valid email address");
            }
        }

        // Validate website URL if provided
        if (request.getWebsite() != null && !request.getWebsite().isEmpty()) {
            String urlRegex = "^(http|https)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(?:/[\\w\\-\\./?%&=]*)?$";
            if (!request.getWebsite().matches(urlRegex)) {
                errors.put("website", "Please provide a valid website URL starting with http:// or https://");
            }
        }

        // Validate location coordinates if provided
        if ((request.getLatitude() != 0 || request.getLongitude() != 0) &&
                (request.getLatitude() < -90 || request.getLatitude() > 90 ||
                        request.getLongitude() < -180 || request.getLongitude() > 180)) {
            errors.put("coordinates",
                    "Please provide valid geographic coordinates (latitude: -90 to 90, longitude: -180 to 180)");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Restaurant validation failed", errors);
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
        dto.setOwnerId(restaurant.getOwnerId());

        // We'll set operating hours in a separate service call if needed

        return dto;
    }
}