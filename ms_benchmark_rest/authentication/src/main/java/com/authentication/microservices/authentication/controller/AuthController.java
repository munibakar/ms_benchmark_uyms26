package com.authentication.microservices.authentication.controller;

import com.authentication.microservices.authentication.dto.request.GoogleLoginRequest;
import com.authentication.microservices.authentication.dto.request.LoginRequest;
import com.authentication.microservices.authentication.dto.request.RegisterRequest;
import com.authentication.microservices.authentication.dto.response.AuthResponse;
import com.authentication.microservices.authentication.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - Authentication Microservice
 * Endpoints: /api/auth/register, /api/auth/login, /api/auth/google
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Google Login
     * POST /auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        log.info("Google login request received");
        AuthResponse response = authService.googleLogin(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        log.info("Logout request received for user ID: {}", userId);
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Health check endpoint
     * GET /auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }
}

