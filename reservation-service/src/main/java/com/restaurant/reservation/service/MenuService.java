package com.restaurant.reservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.reservation.domain.models.MenuCategory;
import com.restaurant.reservation.domain.models.MenuItem;
import com.restaurant.reservation.domain.repositories.MenuCategoryRepository;
import com.restaurant.reservation.domain.repositories.MenuItemRepository;
import com.restaurant.reservation.dto.MenuCategoryDTO;
import com.restaurant.reservation.dto.MenuItemDTO;

/**
 * Service class for menu-related operations.
 * This class provides methods for retrieving and managing menu data.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Service
public class MenuService {
    
    /** Logger for this service */
    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
    
    /** Repository for menu category operations */
    private final MenuCategoryRepository menuCategoryRepository;
    
    /** Repository for menu item operations */
    private final MenuItemRepository menuItemRepository;
    
    /**
     * Constructs a new MenuService with the specified repositories.
     * 
     * @param menuCategoryRepository Repository for menu category operations
     * @param menuItemRepository Repository for menu item operations
     */
    @Autowired
    public MenuService(MenuCategoryRepository menuCategoryRepository, MenuItemRepository menuItemRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuItemRepository = menuItemRepository;
    }
    
    /**
     * Retrieves all active menu categories with their items for a specific restaurant.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of menu categories with their items
     */
    @Transactional(readOnly = true)
    public List<MenuCategoryDTO> getMenuCategoriesWithItems(String restaurantId) {
        logger.info("Retrieving menu categories with items for restaurant: {}", restaurantId);
        
        List<MenuCategory> categories = menuCategoryRepository.findByRestaurantIdAndActiveTrueOrderByDisplayOrderAsc(restaurantId);
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantIdAndActiveTrueAndAvailableTrue(restaurantId);
        
        // Group menu items by category ID
        Map<String, List<MenuItem>> itemsByCategory = menuItems.stream()
                .filter(item -> item.getCategory() != null)
                .collect(Collectors.groupingBy(item -> item.getCategory().getId()));
        
        // Convert to DTOs
        List<MenuCategoryDTO> result = new ArrayList<>();
        for (MenuCategory category : categories) {
            MenuCategoryDTO categoryDTO = convertCategoryToDTO(category);
            
            // Add menu items for this category
            List<MenuItem> categoryItems = itemsByCategory.getOrDefault(category.getId(), new ArrayList<>());
            for (MenuItem item : categoryItems) {
                categoryDTO.addMenuItem(convertItemToDTO(item));
            }
            
            result.add(categoryDTO);
        }
        
        return result;
    }
    
    /**
     * Retrieves a specific menu category with its items.
     * 
     * @param categoryId The ID of the category
     * @return The menu category with its items
     * @throws EntityNotFoundException if the category is not found
     */
    @Transactional(readOnly = true)
    public MenuCategoryDTO getMenuCategoryWithItems(String categoryId) {
        logger.info("Retrieving menu category with items for category: {}", categoryId);
        
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("MenuCategory", categoryId));
        
        if (!category.isActive()) {
            throw new EntityNotFoundException("MenuCategory", categoryId);
        }
        
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantIdAndCategoryIdAndActiveTrueAndAvailableTrue(
                category.getRestaurantId(), categoryId);
        
        // Convert to DTO
        MenuCategoryDTO categoryDTO = convertCategoryToDTO(category);
        
        // Add menu items for this category
        for (MenuItem item : menuItems) {
            categoryDTO.addMenuItem(convertItemToDTO(item));
        }
        
        return categoryDTO;
    }
    
    /**
     * Retrieves all active menu items for a specific restaurant.
     * 
     * @param restaurantId The ID of the restaurant
     * @return List of menu items
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItems(String restaurantId) {
        logger.info("Retrieving menu items for restaurant: {}", restaurantId);
        
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantIdAndActiveTrueAndAvailableTrue(restaurantId);
        
        // Convert to DTOs
        return menuItems.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves a specific menu item.
     * 
     * @param menuItemId The ID of the menu item
     * @return The menu item
     * @throws EntityNotFoundException if the menu item is not found
     */
    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItem(String menuItemId) {
        logger.info("Retrieving menu item: {}", menuItemId);
        
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("MenuItem", menuItemId));
        
        if (!menuItem.isActive()) {
            throw new EntityNotFoundException("MenuItem", menuItemId);
        }
        
        return convertItemToDTO(menuItem);
    }
    
    /**
     * Searches for menu items by name or description.
     * 
     * @param restaurantId The ID of the restaurant
     * @param searchTerm The search term
     * @return List of matching menu items
     */
    @Transactional(readOnly = true)
    public List<MenuItemDTO> searchMenuItems(String restaurantId, String searchTerm) {
        logger.info("Searching menu items for restaurant: {}, term: {}", restaurantId, searchTerm);
        
        List<MenuItem> menuItems = menuItemRepository.searchByNameOrDescription(restaurantId, searchTerm);
        
        // Convert to DTOs
        return menuItems.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Converts a menu category entity to a DTO.
     * 
     * @param category The menu category entity
     * @return The menu category DTO
     */
    private MenuCategoryDTO convertCategoryToDTO(MenuCategory category) {
        MenuCategoryDTO dto = new MenuCategoryDTO();
        dto.setId(category.getId());
        dto.setRestaurantId(category.getRestaurantId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setActive(category.isActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    
    /**
     * Converts a menu item entity to a DTO.
     * 
     * @param menuItem The menu item entity
     * @return The menu item DTO
     */
    private MenuItemDTO convertItemToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setRestaurantId(menuItem.getRestaurantId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        
        if (menuItem.getCategory() != null) {
            dto.setCategoryId(menuItem.getCategory().getId());
            dto.setCategoryName(menuItem.getCategory().getName());
        }
        
        dto.setAvailable(menuItem.isAvailable());
        dto.setActive(menuItem.isActive());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setCreatedAt(menuItem.getCreatedAt());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }
    
    /**
     * Creates or updates a menu category from an event.
     * 
     * @param categoryId The ID of the category
     * @param restaurantId The ID of the restaurant
     * @param name The name of the category
     * @param description The description of the category
     * @param displayOrder The display order of the category
     * @param active Flag indicating if the category is active
     * @return The updated or created menu category
     */
    @Transactional
    public MenuCategory createOrUpdateCategory(String categoryId, String restaurantId, String name, 
                                              String description, int displayOrder, boolean active) {
        logger.info("Creating or updating menu category: {}", categoryId);
        
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElse(new MenuCategory(categoryId, restaurantId, name, description, displayOrder, active));
        
        if (category.getId() != null) {
            // Update existing category
            category.update(name, description, displayOrder, active);
        }
        
        return menuCategoryRepository.save(category);
    }
    
    /**
     * Creates or updates a menu item from an event.
     * 
     * @param menuItemId The ID of the menu item
     * @param restaurantId The ID of the restaurant
     * @param name The name of the menu item
     * @param description The description of the menu item
     * @param price The price of the menu item
     * @param categoryId The ID of the category
     * @param categoryName The name of the category
     * @param available Flag indicating if the menu item is available
     * @param active Flag indicating if the menu item is active
     * @param imageUrl The image URL of the menu item
     * @return The updated or created menu item
     */
    @Transactional
    public MenuItem createOrUpdateMenuItem(String menuItemId, String restaurantId, String name, 
                                          String description, double price, String categoryId, 
                                          String categoryName, boolean available, boolean active, 
                                          String imageUrl) {
        logger.info("Creating or updating menu item: {}", menuItemId);
        
        // Get or create the category
        MenuCategory category = null;
        if (categoryId != null) {
            category = menuCategoryRepository.findById(categoryId)
                    .orElse(new MenuCategory(categoryId, restaurantId, categoryName, "", 0, true));
            
            if (category.getId() == null) {
                // New category
                menuCategoryRepository.save(category);
            }
        }
        
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElse(new MenuItem(menuItemId, restaurantId, name, description, price, 
                                    category, available, active, imageUrl));
        
        if (menuItem.getId() != null) {
            // Update existing menu item
            menuItem.update(name, description, price, category, available, active, imageUrl);
        }
        
        return menuItemRepository.save(menuItem);
    }
}
