package com.restaurant.user.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.common.dto.user.ProfileDTO;
import com.restaurant.common.dto.user.UserDTO;
import com.restaurant.user.dto.UserRegistrationRequest;
import com.restaurant.user.service.ProfileService;
import com.restaurant.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;

    public UserController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserDTO userDTO = userService.registerUser(registrationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.success(userDTO, "User registered successfully"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO<UserDTO>> getCurrentUser(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(ResponseDTO.success(userDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal == #id")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(ResponseDTO.success(userDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ResponseDTO.success(users));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "User deleted successfully"));
    }

    @GetMapping("/{id}/profile")
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal == #id")
    public ResponseEntity<ResponseDTO<ProfileDTO>> getUserProfile(@PathVariable String id) {
        ProfileDTO profileDTO = profileService.getProfileByUserId(id);
        return ResponseEntity.ok(ResponseDTO.success(profileDTO));
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal == #id")
    public ResponseEntity<ResponseDTO<ProfileDTO>> updateUserProfile(
            @PathVariable String id,
            @Valid @RequestBody ProfileDTO profileDTO) {
        ProfileDTO updatedProfile = profileService.updateProfile(id, profileDTO);
        return ResponseEntity.ok(ResponseDTO.success(updatedProfile, "Profile updated successfully"));
    }
}