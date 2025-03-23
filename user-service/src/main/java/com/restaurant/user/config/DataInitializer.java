package com.restaurant.user.config;

import com.restaurant.user.domain.models.Permission;
import com.restaurant.user.domain.models.Role;
import com.restaurant.user.domain.repositories.PermissionRepository;
import com.restaurant.user.domain.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            // Create permissions if they don't exist
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

            // Create roles if they don't exist
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
        };
    }
}