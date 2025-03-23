package com.restaurant.user.api.controllers;

import com.restaurant.common.dto.ResponseDTO;
import com.restaurant.user.dto.LoginRequest;
import com.restaurant.user.dto.LoginResponse;
import com.restaurant.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginResponse>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse loginResponse = authService.login(loginRequest, request);
        return ResponseEntity.ok(ResponseDTO.success(loginResponse));
    }
}