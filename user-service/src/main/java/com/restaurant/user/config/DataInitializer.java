package com.restaurant.user.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restaurant.user.domain.models.Permission;
import com.restaurant.user.domain.models.Profile;
import com.restaurant.user.domain.models.Role;
import com.restaurant.user.domain.models.User;
import com.restaurant.user.domain.repositories.PermissionRepository;
import com.restaurant.user.domain.repositories.ProfileRepository;
import com.restaurant.user.domain.repositories.RoleRepository;
import com.restaurant.user.domain.repositories.UserRepository;

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    // เพิ่ม constructor ที่รับ dependencies ทั้งหมด
    public DataInitializer(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          ProfileRepository profileRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @org.springframework.context.annotation.Profile("!test")
    public CommandLineRunner initData() {
        return args -> {
            // สร้าง permissions
            List<String> permissionNames = Arrays.asList(
                    "user:read", "user:write", "user:delete",
                    "profile:read", "profile:write",
                    "restaurant:read"
            );
            
            for (String name : permissionNames) {
                if (!permissionRepository.findByName(name).isPresent()) {
                    Permission permission = new Permission(name, "Permission to " + name);
                    permissionRepository.save(permission);
                }
            }

            // สร้าง roles
            if (!roleRepository.findByName("USER").isPresent()) {
                Role userRole = new Role("USER", "Standard user role");
                userRole.addPermission(permissionRepository.findByName("user:read").get());
                userRole.addPermission(permissionRepository.findByName("profile:read").get());
                userRole.addPermission(permissionRepository.findByName("profile:write").get());
                userRole.addPermission(permissionRepository.findByName("restaurant:read").get());
                roleRepository.save(userRole);
            }

            if (!roleRepository.findByName("ADMIN").isPresent()) {
                Role adminRole = new Role("ADMIN", "Administrator role");
                adminRole.addPermission(permissionRepository.findByName("user:read").get());
                adminRole.addPermission(permissionRepository.findByName("user:write").get());
                adminRole.addPermission(permissionRepository.findByName("user:delete").get());
                adminRole.addPermission(permissionRepository.findByName("profile:read").get());
                adminRole.addPermission(permissionRepository.findByName("profile:write").get());
                adminRole.addPermission(permissionRepository.findByName("restaurant:read").get());
                roleRepository.save(adminRole);
            }

            // สร้าง admin user
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setEnabled(true);
                
                // กำหนด ADMIN role
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Admin role not found"));
                adminUser.addRole(adminRole);
                
                // Save the user first to get the ID
                adminUser = userRepository.save(adminUser);
                
                // Create profile for admin
                Profile adminProfile = new Profile(adminUser);
                adminProfile.setFirstName("Admin");
                adminProfile.setLastName("User");
                adminProfile.setPhoneNumber("0000000000");
                
                // Save the profile
                profileRepository.save(adminProfile);
                
                // Update user with profile reference
                adminUser.setProfile(adminProfile);
                userRepository.save(adminUser);
                
                System.out.println("Default admin user created with username: admin and password: admin123");
            }
        };
    }
}