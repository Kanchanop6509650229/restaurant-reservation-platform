package com.restaurant.reservation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation used to inject the currently authenticated user into controller methods.
 * This annotation can be used on method parameters to automatically resolve the current user's
 * information from the Spring Security context.
 * 
 * The annotation is processed by {@link CurrentUserArgumentResolver} which extracts the user
 * information from the security context and injects it into the annotated parameter.
 * 
 * Example usage:
 * {@code
 * @GetMapping("/profile")
 * public ResponseEntity<UserProfile> getProfile(@CurrentUser String userId) {
 *     // userId will contain the ID of the currently authenticated user
 *     return userService.getProfile(userId);
 * }
 * }
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}