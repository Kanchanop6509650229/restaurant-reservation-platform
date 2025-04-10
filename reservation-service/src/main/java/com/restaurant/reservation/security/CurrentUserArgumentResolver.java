package com.restaurant.reservation.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Spring MVC argument resolver that processes the {@link CurrentUser} annotation.
 * This resolver extracts the current user's information from the Spring Security context
 * and injects it into controller method parameters annotated with {@link CurrentUser}.
 * 
 * The resolver supports String parameters and extracts either the username from UserDetails
 * or the principal object itself if it's a String.
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Determines if this resolver can handle the given method parameter.
     * Returns true if the parameter is of type String and is annotated with {@link CurrentUser}.
     *
     * @param parameter the method parameter to check
     * @return true if this resolver can handle the parameter, false otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class) &&
               parameter.hasParameterAnnotation(CurrentUser.class);
    }

    /**
     * Resolves the argument value for the given method parameter.
     * Extracts the current user's information from the Spring Security context.
     * 
     * The resolution process:
     * 1. Gets the current Authentication from SecurityContextHolder
     * 2. Checks if the user is authenticated
     * 3. If the principal is UserDetails, returns the username
     * 4. If the principal is a String, returns it directly
     * 5. Returns null if no valid user information is found
     *
     * @param parameter the method parameter to resolve
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest the current web request
     * @param binderFactory a factory for creating WebDataBinder instances
     * @return the resolved argument value, or null if no valid user is found
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest,
                                 WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        
        if (principal instanceof String) {
            return principal;
        }
        
        return null;
    }
}