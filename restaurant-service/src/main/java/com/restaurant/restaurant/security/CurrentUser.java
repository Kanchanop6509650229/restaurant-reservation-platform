package com.restaurant.restaurant.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for injecting the current authenticated user into controller methods.
 * This annotation can be used on method parameters to automatically resolve
 * the currently authenticated user's information.
 * 
 * Usage example:
 * public ResponseEntity<?> someEndpoint(@CurrentUser String userId) { ... }
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}