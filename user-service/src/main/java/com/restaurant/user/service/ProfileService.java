package com.restaurant.user.service;

import com.restaurant.common.dto.user.ProfileDTO;
import com.restaurant.common.events.user.ProfileUpdatedEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.user.domain.models.Profile;
import com.restaurant.user.domain.models.User;
import com.restaurant.user.domain.repositories.ProfileRepository;
import com.restaurant.user.domain.repositories.UserRepository;
import com.restaurant.user.dto.UserRegistrationRequest;
import com.restaurant.user.kafka.producers.UserEventProducer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public ProfileService(ProfileRepository profileRepository, 
                          UserRepository userRepository,
                          UserEventProducer userEventProducer) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    @Transactional
    public Profile createProfile(String userId, UserRegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        Profile profile = new Profile(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());

        return profileRepository.save(profile);
    }

    public ProfileDTO getProfileByUserId(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile for user", userId));
        return convertToDTO(profile);
    }

    @Transactional
    public ProfileDTO updateProfile(String userId, ProfileDTO profileDTO) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile for user", userId));

        // Update first name
        if (profileDTO.getFirstName() != null && !profileDTO.getFirstName().equals(profile.getFirstName())) {
            String oldValue = profile.getFirstName();
            profile.setFirstName(profileDTO.getFirstName());
            
            // Publish event
            userEventProducer.publishProfileUpdatedEvent(new ProfileUpdatedEvent(
                    userId,
                    "firstName",
                    oldValue,
                    profileDTO.getFirstName()
            ));
        }

        // Update last name
        if (profileDTO.getLastName() != null && !profileDTO.getLastName().equals(profile.getLastName())) {
            String oldValue = profile.getLastName();
            profile.setLastName(profileDTO.getLastName());
            
            // Publish event
            userEventProducer.publishProfileUpdatedEvent(new ProfileUpdatedEvent(
                    userId,
                    "lastName",
                    oldValue,
                    profileDTO.getLastName()
            ));
        }

        // Update phone number
        if (profileDTO.getPhoneNumber() != null && !profileDTO.getPhoneNumber().equals(profile.getPhoneNumber())) {
            String oldValue = profile.getPhoneNumber();
            profile.setPhoneNumber(profileDTO.getPhoneNumber());
            
            // Publish event
            userEventProducer.publishProfileUpdatedEvent(new ProfileUpdatedEvent(
                    userId,
                    "phoneNumber",
                    oldValue,
                    profileDTO.getPhoneNumber()
            ));
        }

        Profile updatedProfile = profileRepository.save(profile);
        return convertToDTO(updatedProfile);
    }

    private ProfileDTO convertToDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        // Map profile fields to DTO
        dto.setUserId(profile.getUser().getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        // Map other fields
        return dto;
    }
}