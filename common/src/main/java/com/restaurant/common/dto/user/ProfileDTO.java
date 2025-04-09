package com.restaurant.common.dto.user;

import java.time.LocalDate;

/**
 * Data Transfer Object for user profile information.
 * This class represents a user's extended profile details including
 * personal information, contact details, and preferences.
 *
 * @author Restaurant Team
 * @version 1.0
 */
public class ProfileDTO {
    /** ID of the user this profile belongs to */
    private String userId;
    
    /** User's first name */
    private String firstName;
    
    /** User's last name */
    private String lastName;
    
    /** User's date of birth */
    private LocalDate dateOfBirth;
    
    /** User's contact phone number */
    private String phoneNumber;
    
    /** User's physical address */
    private String address;
    
    /** URL to the user's profile picture */
    private String avatarUrl;
    
    /** User's preferred language for the application */
    private String preferredLanguage;
    
    /** Whether the user has consented to marketing communications */
    private boolean marketingConsent;
    
    /**
     * Default constructor.
     */
    public ProfileDTO() {
    }
    
    /**
     * Gets the ID of the user this profile belongs to.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user this profile belongs to.
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * Gets the user's date of birth.
     *
     * @return The date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the user's date of birth.
     *
     * @param dateOfBirth The date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
     * Gets the user's address.
     *
     * @return The physical address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the user's address.
     *
     * @param address The physical address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the URL to the user's profile picture.
     *
     * @return The avatar URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the URL to the user's profile picture.
     *
     * @param avatarUrl The avatar URL to set
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Gets the user's preferred language.
     *
     * @return The preferred language code
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Sets the user's preferred language.
     *
     * @param preferredLanguage The preferred language code to set
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * Checks if the user has consented to marketing communications.
     *
     * @return true if marketing consent is given, false otherwise
     */
    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    /**
     * Sets whether the user has consented to marketing communications.
     *
     * @param marketingConsent true to give marketing consent, false to revoke it
     */
    public void setMarketingConsent(boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }
}