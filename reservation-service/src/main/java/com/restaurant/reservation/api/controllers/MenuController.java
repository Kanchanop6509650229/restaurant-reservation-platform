package com.restaurant.reservation.api.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.reservation.dto.MenuCategoryDTO;
import com.restaurant.reservation.dto.MenuItemDTO;
import com.restaurant.reservation.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for managing restaurant menu items.
 * Provides endpoints for retrieving menu categories and items.
 * All endpoints are publicly accessible without authentication.
 *
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/menus")
@Tag(name = "Menu Management", description = "APIs for browsing restaurant menus and items")
public class MenuController {

    /** Logger for this controller */
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    /** Service for menu operations */
    private final MenuService menuService;

    /**
     * Constructs a new MenuController with the specified service.
     *
     * @param menuService Service for menu operations
     */
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Retrieves all menu categories with their items for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing the list of menu categories with their
     *         items
     */
    @GetMapping("/restaurants/{restaurantId}/categories")
    @Operation(
        summary = "Get restaurant menu categories",
        description = "Retrieves all menu categories with their items for a specific restaurant"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Menu categories retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuCategoryDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid restaurant ID")
    })
    public ResponseEntity<ResponseDTO<List<MenuCategoryDTO>>> getMenuCategories(
            @Parameter(description = "ID of the restaurant", required = true) @PathVariable String restaurantId) {
        logger.info("Retrieving menu categories for restaurant: {}", restaurantId);

        try {
            List<MenuCategoryDTO> categories = menuService.getMenuCategoriesWithItems(restaurantId);
            return ResponseEntity.ok(ResponseDTO.success(categories, "Menu categories retrieved successfully"));
        } catch (Exception e) {
            logger.error("Failed to retrieve menu categories for restaurant: {}", restaurantId, e);
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Failed to retrieve menu categories: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a specific menu category with its items.
     *
     * @param categoryId The ID of the category
     * @return ResponseEntity containing the menu category with its items
     */
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ResponseDTO<MenuCategoryDTO>> getMenuCategory(@PathVariable String categoryId) {
        logger.info("Retrieving menu category: {}", categoryId);

        try {
            MenuCategoryDTO category = menuService.getMenuCategoryWithItems(categoryId);
            return ResponseEntity.ok(ResponseDTO.success(category, "Menu category retrieved successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Menu category not found: {}", categoryId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to retrieve menu category: {}", categoryId, e);
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Failed to retrieve menu category: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant
     * @return ResponseEntity containing the list of menu items
     */
    @GetMapping("/restaurants/{restaurantId}/items")
    public ResponseEntity<ResponseDTO<List<MenuItemDTO>>> getMenuItems(@PathVariable String restaurantId) {
        logger.info("Retrieving menu items for restaurant: {}", restaurantId);

        try {
            List<MenuItemDTO> items = menuService.getMenuItems(restaurantId);
            return ResponseEntity.ok(ResponseDTO.success(items, "Menu items retrieved successfully"));
        } catch (Exception e) {
            logger.error("Failed to retrieve menu items for restaurant: {}", restaurantId, e);
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Failed to retrieve menu items: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a specific menu item.
     *
     * @param itemId The ID of the menu item
     * @return ResponseEntity containing the menu item
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ResponseDTO<MenuItemDTO>> getMenuItem(@PathVariable String itemId) {
        logger.info("Retrieving menu item: {}", itemId);

        try {
            MenuItemDTO item = menuService.getMenuItem(itemId);
            return ResponseEntity.ok(ResponseDTO.success(item, "Menu item retrieved successfully"));
        } catch (EntityNotFoundException e) {
            logger.warn("Menu item not found: {}", itemId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to retrieve menu item: {}", itemId, e);
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Failed to retrieve menu item: " + e.getMessage()));
        }
    }

    /**
     * Searches for menu items by name or description.
     *
     * @param restaurantId The ID of the restaurant
     * @param query        The search query
     * @return ResponseEntity containing the list of matching menu items
     */
    @GetMapping("/restaurants/{restaurantId}/search")
    public ResponseEntity<ResponseDTO<List<MenuItemDTO>>> searchMenuItems(
            @PathVariable String restaurantId,
            @RequestParam String query) {
        logger.info("Searching menu items for restaurant: {}, query: {}", restaurantId, query);

        try {
            List<MenuItemDTO> items = menuService.searchMenuItems(restaurantId, query);
            return ResponseEntity.ok(ResponseDTO.success(items, "Menu items search completed successfully"));
        } catch (Exception e) {
            logger.error("Failed to search menu items for restaurant: {}", restaurantId, e);
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Failed to search menu items: " + e.getMessage()));
        }
    }
}
