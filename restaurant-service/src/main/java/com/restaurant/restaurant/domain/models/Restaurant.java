package com.restaurant.restaurant.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entity class representing a restaurant in the system.
 * This class provides:
 * - Restaurant identification and contact information
 * - Geographic location tracking
 * - Capacity and rating management
 * - Relationship management with tables, operating hours, branches, and staff
 * 
 * The entity is mapped to the 'restaurants' table in the database
 * and includes relationships with multiple other entities.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "restaurants")
public class Restaurant {

    /** Unique identifier for the restaurant */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Name of the restaurant */
    @Column(nullable = false)
    private String name;

    /** Detailed description of the restaurant */
    @Column(length = 1000)
    private String description;

    /** Street address of the restaurant */
    @Column(nullable = false)
    private String address;

    /** ID of the restaurant owner */
    @Column(nullable = false)
    private String ownerId;

    /** City where the restaurant is located */
    private String city;

    /** State or province where the restaurant is located */
    private String state;

    /** Postal or ZIP code of the restaurant location */
    private String zipCode;

    /** Country where the restaurant is located */
    private String country;

    /** Contact phone number of the restaurant */
    @Column(nullable = false)
    private String phoneNumber;

    /** Contact email address of the restaurant */
    private String email;

    /** Website URL of the restaurant */
    private String website;
    
    /** Geographic point location of the restaurant (transient) */
    @Transient
    private Point location;
    
    /** Latitude coordinate of the restaurant location */
    private double latitude;

    /** Longitude coordinate of the restaurant location */
    private double longitude;

    /** Type of cuisine served at the restaurant */
    @Column(nullable = false)
    private String cuisineType;

    /** Total seating capacity of the restaurant */
    private int totalCapacity;
    
    /** Average rating of the restaurant (precision: 3, scale: 2) */
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    /** Total number of ratings received */
    private int totalRatings;

    /** Active status of the restaurant */
    private boolean active = true;

    /** Set of tables in the restaurant */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantTable> tables = new HashSet<>();

    /** Set of operating hours for the restaurant */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OperatingHours> operatingHours = new HashSet<>();

    /** Set of branches of the restaurant */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Branch> branches = new HashSet<>();

    /** Set of staff members working at the restaurant */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Staff> staff = new HashSet<>();

    /** Timestamp when the restaurant was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the restaurant was last updated */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets the creation and update timestamps before persisting a new entity.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Updates the timestamp when the entity is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a table to the restaurant and sets up the bidirectional relationship.
     *
     * @param table The table to add
     */
    public void addTable(RestaurantTable table) {
        tables.add(table);
        table.setRestaurant(this);
    }

    /**
     * Removes a table from the restaurant and clears the relationship.
     *
     * @param table The table to remove
     */
    public void removeTable(RestaurantTable table) {
        tables.remove(table);
        table.setRestaurant(null);
    }

    /**
     * Adds operating hours to the restaurant and sets up the bidirectional relationship.
     *
     * @param hours The operating hours to add
     */
    public void addOperatingHours(OperatingHours hours) {
        operatingHours.add(hours);
        hours.setRestaurant(this);
    }

    /**
     * Removes operating hours from the restaurant and clears the relationship.
     *
     * @param hours The operating hours to remove
     */
    public void removeOperatingHours(OperatingHours hours) {
        operatingHours.remove(hours);
        hours.setRestaurant(null);
    }

    /**
     * Adds a branch to the restaurant and sets up the bidirectional relationship.
     *
     * @param branch The branch to add
     */
    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setRestaurant(this);
    }

    /**
     * Removes a branch from the restaurant and clears the relationship.
     *
     * @param branch The branch to remove
     */
    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setRestaurant(null);
    }

    /**
     * Adds a staff member to the restaurant and sets up the bidirectional relationship.
     *
     * @param staffMember The staff member to add
     */
    public void addStaff(Staff staffMember) {
        staff.add(staffMember);
        staffMember.setRestaurant(this);
    }

    /**
     * Removes a staff member from the restaurant and clears the relationship.
     *
     * @param staffMember The staff member to remove
     */
    public void removeStaff(Staff staffMember) {
        staff.remove(staffMember);
        staffMember.setRestaurant(null);
    }

    /**
     * Default constructor for JPA.
     */
    public Restaurant() {
    }

    /**
     * Constructs a new Restaurant with basic information.
     *
     * @param name The name of the restaurant
     * @param address The street address of the restaurant
     * @param phoneNumber The contact phone number
     * @param cuisineType The type of cuisine served
     */
    public Restaurant(String name, String address, String phoneNumber, String cuisineType) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the unique identifier of the restaurant.
     *
     * @return The restaurant ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the restaurant.
     *
     * @param id The restaurant ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the restaurant.
     *
     * @return The restaurant name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the restaurant.
     *
     * @param name The restaurant name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the restaurant.
     *
     * @return The restaurant description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the restaurant.
     *
     * @param description The restaurant description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the street address of the restaurant.
     *
     * @return The restaurant address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the street address of the restaurant.
     *
     * @param address The restaurant address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the city where the restaurant is located.
     *
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the restaurant is located.
     *
     * @param city The city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state or province where the restaurant is located.
     *
     * @return The state or province name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province where the restaurant is located.
     *
     * @param state The state or province name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the postal or ZIP code of the restaurant location.
     *
     * @return The postal or ZIP code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the postal or ZIP code of the restaurant location.
     *
     * @param zipCode The postal or ZIP code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the country where the restaurant is located.
     *
     * @return The country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country where the restaurant is located.
     *
     * @param country The country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the contact phone number of the restaurant.
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the contact phone number of the restaurant.
     *
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the contact email address of the restaurant.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact email address of the restaurant.
     *
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the website URL of the restaurant.
     *
     * @return The website URL
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website URL of the restaurant.
     *
     * @param website The website URL
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets the geographic point location of the restaurant.
     *
     * @return The location point
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Sets the geographic point location of the restaurant.
     *
     * @param location The location point
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Gets the latitude coordinate of the restaurant location.
     *
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of the restaurant location.
     *
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate of the restaurant location.
     *
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of the restaurant location.
     *
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the type of cuisine served at the restaurant.
     *
     * @return The cuisine type
     */
    public String getCuisineType() {
        return cuisineType;
    }

    /**
     * Sets the type of cuisine served at the restaurant.
     *
     * @param cuisineType The cuisine type
     */
    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    /**
     * Gets the total seating capacity of the restaurant.
     *
     * @return The total capacity
     */
    public int getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the total seating capacity of the restaurant.
     *
     * @param totalCapacity The total capacity
     */
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    /**
     * Gets the average rating of the restaurant.
     *
     * @return The average rating
     */
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of the restaurant.
     *
     * @param averageRating The average rating
     */
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Gets the total number of ratings received.
     *
     * @return The total number of ratings
     */
    public int getTotalRatings() {
        return totalRatings;
    }

    /**
     * Sets the total number of ratings received.
     *
     * @param totalRatings The total number of ratings
     */
    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    /**
     * Checks if the restaurant is active.
     *
     * @return true if the restaurant is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the restaurant is active.
     *
     * @param active The active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the set of tables in the restaurant.
     *
     * @return The set of tables
     */
    public Set<RestaurantTable> getTables() {
        return tables;
    }

    /**
     * Sets the set of tables in the restaurant.
     *
     * @param tables The set of tables
     */
    public void setTables(Set<RestaurantTable> tables) {
        this.tables = tables;
    }

    /**
     * Gets the set of operating hours for the restaurant.
     *
     * @return The set of operating hours
     */
    public Set<OperatingHours> getOperatingHours() {
        return operatingHours;
    }

    /**
     * Sets the set of operating hours for the restaurant.
     *
     * @param operatingHours The set of operating hours
     */
    public void setOperatingHours(Set<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
    }

    /**
     * Gets the set of branches of the restaurant.
     *
     * @return The set of branches
     */
    public Set<Branch> getBranches() {
        return branches;
    }

    /**
     * Sets the set of branches of the restaurant.
     *
     * @param branches The set of branches
     */
    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
    }

    /**
     * Gets the set of staff members working at the restaurant.
     *
     * @return The set of staff members
     */
    public Set<Staff> getStaff() {
        return staff;
    }

    /**
     * Sets the set of staff members working at the restaurant.
     *
     * @param staff The set of staff members
     */
    public void setStaff(Set<Staff> staff) {
        this.staff = staff;
    }

    /**
     * Gets the timestamp when the restaurant was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the timestamp when the restaurant was last updated.
     *
     * @return The update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Gets the ID of the restaurant owner.
     *
     * @return The owner ID
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the ID of the restaurant owner.
     *
     * @param ownerId The owner ID
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}