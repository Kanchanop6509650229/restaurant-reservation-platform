package com.restaurant.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurant.common.dto.user.UserDTO;
import com.restaurant.common.events.user.UserRegisteredEvent;
import com.restaurant.common.exceptions.EntityNotFoundException;
import com.restaurant.common.exceptions.ValidationException;
import com.restaurant.user.domain.models.Role;
import com.restaurant.user.domain.models.User;
import com.restaurant.user.domain.repositories.RoleRepository;
import com.restaurant.user.domain.repositories.UserRepository;
import com.restaurant.user.dto.UserRegistrationRequest;
import com.restaurant.user.kafka.producers.UserEventProducer;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    private final UserEventProducer userEventProducer;

    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder,
                       ProfileService profileService,
                       UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.userEventProducer = userEventProducer;
    }

    @Transactional
    public UserDTO registerUser(UserRegistrationRequest registrationRequest) {
        // Validate input
        validateRegistrationRequest(registrationRequest);

        // Create new user
        User user = new User(
                registrationRequest.getUsername(),
                registrationRequest.getEmail(),
                passwordEncoder.encode(registrationRequest.getPassword())
        );

        // Assign default role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role", "USER"));
        user.addRole(userRole);

        // Save user
        User savedUser = userRepository.save(user);

        // Create profile
        profileService.createProfile(savedUser.getId(), registrationRequest);

        // Publish event
        userEventProducer.publishUserRegisteredEvent(new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        ));

        // Return DTO
        return convertToDTO(savedUser);
    }

    private void validateRegistrationRequest(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("username", "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("email", "Email already exists");
        }
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
        return convertToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username: " + username));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
        userRepository.delete(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        
        // Set additional fields
        dto.setEnabled(user.isEnabled());
        
        // Set profile information if available
        if (user.getProfile() != null) {
            dto.setFirstName(user.getProfile().getFirstName());
            dto.setLastName(user.getProfile().getLastName());
            dto.setPhoneNumber(user.getProfile().getPhoneNumber());
        }
        
        // Set time information
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // Set roles if available
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
}