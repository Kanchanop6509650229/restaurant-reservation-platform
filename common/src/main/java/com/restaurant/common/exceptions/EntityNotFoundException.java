package com.restaurant.common.exceptions;

public class EntityNotFoundException extends BaseException {
    private final String entityType;
    private final String entityId;
    
    public EntityNotFoundException(String entityType, String entityId) {
        super("Entity not found: " + entityType + " with id " + entityId, "ENTITY_NOT_FOUND");
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getEntityId() {
        return entityId;
    }
}