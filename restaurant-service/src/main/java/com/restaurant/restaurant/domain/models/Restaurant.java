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

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Column(nullable = false)
    private String phoneNumber;

    private String email;
    private String website;
    
    // Geo-location for mapping and distance calculation
    @Transient
    private Point location;
    
    private double latitude;
    private double longitude;

    @Column(nullable = false)
    private String cuisineType;

    private int totalCapacity;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    private int totalRatings;

    private boolean active = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RestaurantTable> tables = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OperatingHours> operatingHours = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Branch> branches = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Staff> staff = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for entity relationships
    public void addTable(RestaurantTable table) {
        tables.add(table);
        table.setRestaurant(this);
    }

    public void removeTable(RestaurantTable table) {
        tables.remove(table);
        table.setRestaurant(null);
    }

    public void addOperatingHours(OperatingHours hours) {
        operatingHours.add(hours);
        hours.setRestaurant(this);
    }

    public void removeOperatingHours(OperatingHours hours) {
        operatingHours.remove(hours);
        hours.setRestaurant(null);
    }

    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setRestaurant(this);
    }

    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setRestaurant(null);
    }

    public void addStaff(Staff staffMember) {
        staff.add(staffMember);
        staffMember.setRestaurant(this);
    }

    public void removeStaff(Staff staffMember) {
        staff.remove(staffMember);
        staffMember.setRestaurant(null);
    }

    // Constructors
    public Restaurant() {
    }

    public Restaurant(String name, String address, String phoneNumber, String cuisineType) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cuisineType = cuisineType;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<RestaurantTable> getTables() {
        return tables;
    }

    public void setTables(Set<RestaurantTable> tables) {
        this.tables = tables;
    }

    public Set<OperatingHours> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(Set<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
    }

    public Set<Staff> getStaff() {
        return staff;
    }

    public void setStaff(Set<Staff> staff) {
        this.staff = staff;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}