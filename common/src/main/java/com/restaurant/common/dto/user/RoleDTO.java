package com.restaurant.common.dto.user;

import java.util.Set;

/**
 * Data Transfer Object for user roles.
 * This class represents a role's information including its name,
 * description, and associated permissions.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class RoleDTO {
    /** Unique identifier for the role */
    private String id;
    
    /** Name of the role */
    private String name;
    
    /** Description of the role's purpose and responsibilities */
    private String description;
    
    /** Set of permissions associated with this role */
    private Set<String> permissions;
    
    /**
     * Default constructor.
     */
    public RoleDTO() {
    }
    
    /**
     * Constructor with basic role information.
     *
     * @param id The role's unique identifier
     * @param name The role's name
     */
    public RoleDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Gets the role's unique identifier.
     *
     * @return The role ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the role's unique identifier.
     *
     * @param id The role ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the role's name.
     *
     * @return The role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the role's name.
     *
     * @param name The role name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the role's description.
     *
     * @return The role description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the role's description.
     *
     * @param description The role description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the set of permissions associated with this role.
     *
     * @return Set of permission strings
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Sets the permissions associated with this role.
     *
     * @param permissions Set of permission strings to set
     */
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}