package com.restaurant.restaurant.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Argument resolver for the @CurrentUser annotation.
 * This resolver automatically injects the current authenticated user's ID
 * into controller method parameters annotated with @CurrentUser.
 * 
 * The resolver works with Spring's web argument resolution mechanism to:
 * - Check if a parameter is annotated with @CurrentUser
 * - Extract the current user's ID from the security context
 * - Provide the ID value to the controller method
 * 
 * @author Restaurant Reservation Team
 * @version 1.0
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Determines if this resolver supports the given method parameter.
     * Returns true if the parameter is of type String and has the @CurrentUser annotation.
     *
     * @param parameter The method parameter to check
     * @return true if this resolver can handle the parameter, false otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class) &&
               parameter.hasParameterAnnotation(CurrentUser.class);
    }

    /**
     * Resolves the current user ID from the security context for injection into the method parameter.
     * Returns null if no authentication is present or the user is not authenticated.
     *
     * @param parameter The method parameter being resolved
     * @param mavContainer The ModelAndViewContainer for the current request
     * @param webRequest The current request
     * @param binderFactory The factory for creating WebDataBinder instances
     * @return The current user's ID or null if not authenticated
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
        
        return authentication.getName();
    }
}