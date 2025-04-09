package com.restaurant.common.exceptions;

/**
 * Exception thrown when a requested entity cannot be found in the system.
 * This exception is typically thrown when attempting to retrieve, update, or delete
 * an entity that does not exist in the database.
 * Extends BaseException with error code "ENTITY_NOT_FOUND".
 */
public class EntityNotFoundException extends BaseException {
    /**
     * The type of entity that was not found (e.g., "User", "Restaurant", "Reservation").
     */
    private final String entityType;
    
    /**
     * The unique identifier that was used to search for the entity.
     */
    private final String entityId;
    
    /**
     * Constructs a new EntityNotFoundException with details about the missing entity.
     *
     * @param entityType The type of entity that was not found
     * @param entityId   The ID that was used to search for the entity
     */
    public EntityNotFoundException(String entityType, String entityId) {
        super("Entity not found: " + entityType + " with id " + entityId, "ENTITY_NOT_FOUND");
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    /**
     * Gets the type of entity that was not found.
     *
     * @return The entity type as a String
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Gets the ID that was used to search for the entity.
     *
     * @return The entity ID as a String
     */
    public String getEntityId() {
        return entityId;
    }
}