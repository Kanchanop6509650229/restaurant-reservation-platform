package com.restaurant.restaurant.domain.models;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class representing a branch location of a restaurant.
 * This class provides:
 * - Branch identification and contact information
 * - Geographic location tracking
 * - Address management
 * - Main branch designation
 * 
 * The entity is mapped to the 'branches' table in the database
 * and includes relationships with the Restaurant entity.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Entity
@Table(name = "branches")
public class Branch {

    /** Unique identifier for the branch */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The restaurant this branch belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    /** Name of the branch */
    @Column(nullable = false)
    private String name;

    /** Street address of the branch */
    @Column(nullable = false)
    private String address;

    /** City where the branch is located */
    private String city;

    /** State or province where the branch is located */
    private String state;

    /** Postal or ZIP code of the branch location */
    private String zipCode;

    /** Country where the branch is located */
    private String country;

    /** Contact phone number of the branch */
    private String phoneNumber;

    /** Contact email address of the branch */
    private String email;

    /** Geographic point location of the branch */
    private Point location;

    /** Latitude coordinate of the branch location */
    private double latitude;

    /** Longitude coordinate of the branch location */
    private double longitude;

    /** Flag indicating if this is the main branch of the restaurant */
    private boolean isMainBranch = false;

    /** Active status of the branch */
    private boolean active = true;

    /**
     * Default constructor for JPA.
     */
    public Branch() {
    }

    /**
     * Constructs a new Branch with basic information.
     *
     * @param restaurant The restaurant this branch belongs to
     * @param name The name of the branch
     * @param address The street address of the branch
     */
    public Branch(Restaurant restaurant, String name, String address) {
        this.restaurant = restaurant;
        this.name = name;
        this.address = address;
    }

    /**
     * Gets the unique identifier of the branch.
     *
     * @return The branch ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the branch.
     *
     * @param id The branch ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the restaurant this branch belongs to.
     *
     * @return The associated restaurant
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * Sets the restaurant this branch belongs to.
     *
     * @param restaurant The associated restaurant
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    /**
     * Gets the name of the branch.
     *
     * @return The branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the branch.
     *
     * @param name The branch name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the street address of the branch.
     *
     * @return The branch address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the street address of the branch.
     *
     * @param address The branch address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the city where the branch is located.
     *
     * @return The city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the branch is located.
     *
     * @param city The city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state or province where the branch is located.
     *
     * @return The state or province name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province where the branch is located.
     *
     * @param state The state or province name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the postal or ZIP code of the branch location.
     *
     * @return The postal or ZIP code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the postal or ZIP code of the branch location.
     *
     * @param zipCode The postal or ZIP code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the country where the branch is located.
     *
     * @return The country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country where the branch is located.
     *
     * @param country The country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the contact phone number of the branch.
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the contact phone number of the branch.
     *
     * @param phoneNumber The phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the contact email address of the branch.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the contact email address of the branch.
     *
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the geographic point location of the branch.
     *
     * @return The location point
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Sets the geographic point location of the branch.
     *
     * @param location The location point
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Gets the latitude coordinate of the branch location.
     *
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of the branch location.
     *
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate of the branch location.
     *
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of the branch location.
     *
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Checks if this is the main branch of the restaurant.
     *
     * @return true if this is the main branch, false otherwise
     */
    public boolean isMainBranch() {
        return isMainBranch;
    }

    /**
     * Sets whether this is the main branch of the restaurant.
     *
     * @param mainBranch The main branch status
     */
    public void setMainBranch(boolean mainBranch) {
        isMainBranch = mainBranch;
    }

    /**
     * Checks if the branch is active.
     *
     * @return true if the branch is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether the branch is active.
     *
     * @param active The active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}