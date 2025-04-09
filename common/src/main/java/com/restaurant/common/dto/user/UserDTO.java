package com.restaurant.common.dto.user;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for user information.
 * This class represents a user's complete profile including
 * personal details, contact information, and system roles.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class UserDTO {
    /** Unique identifier for the user */
    private String id;
    
    /** User's login username */
    private String username;
    
    /** User's email address */
    private String email;
    
    /** User's first name */
    private String firstName;
    
    /** User's last name */
    private String lastName;
    
    /** User's contact phone number */
    private String phoneNumber;
    
    /** Whether the user account is enabled */
    private boolean enabled;
    
    /** Timestamp when the user account was created */
    private LocalDateTime createdAt;
    
    /** Timestamp when the user account was last updated */
    private LocalDateTime updatedAt;
    
    /** Set of role names assigned to the user */
    private Set<String> roles;
    
    /**
     * Default constructor.
     */
    public UserDTO() {
    }
    
    /**
     * Constructor with basic user information.
     *
     * @param id The user's unique identifier
     * @param username The user's login username
     * @param email The user's email address
     */
    public UserDTO(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    /**
     * Gets the user's unique identifier.
     *
     * @return The user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id The user ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user's login username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's login username.
     *
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's email address.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName The last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's phone number.
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber The phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Checks if the user account is enabled.
     *
     * @return true if the account is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the user account is enabled.
     *
     * @param enabled true to enable the account, false to disable it
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the timestamp when the user account was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the user account was created.
     *
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the user account was last updated.
     *
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the user account was last updated.
     *
     * @param updatedAt The last update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the set of role names assigned to the user.
     *
     * @return Set of role names
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Sets the roles assigned to the user.
     *
     * @param roles Set of role names to assign
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}