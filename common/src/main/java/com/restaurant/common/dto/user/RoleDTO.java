package com.restaurant.common.dto.user;

import java.util.Set;

public class RoleDTO {
    private String id;
    private String name;
    private String description;
    private Set<String> permissions;
    
    // Constructors
    public RoleDTO() {
    }
    
    public RoleDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}