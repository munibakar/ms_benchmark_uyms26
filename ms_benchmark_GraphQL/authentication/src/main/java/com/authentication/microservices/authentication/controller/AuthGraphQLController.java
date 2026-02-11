package com.authentication.microservices.authentication.controller;

import com.authentication.microservices.authentication.dto.request.GoogleLoginRequest;
import com.authentication.microservices.authentication.dto.request.LoginRequest;
import com.authentication.microservices.authentication.dto.request.RegisterRequest;
import com.authentication.microservices.authentication.dto.response.AuthResponse;
import com.authentication.microservices.authentication.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Authentication GraphQL Controller
 * 
 * Apollo Federation subgraph olarak çalışır.
 * Register, Login, Logout mutation'larını handle eder.
 */
@Controller
public class AuthGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(AuthGraphQLController.class);

    private final AuthService authService;

    public AuthGraphQLController(AuthService authService) {
        this.authService = authService;
    }


    @QueryMapping
    public String authHealth() {
        return "Authentication Service is running!";
    }

 

    /**
     * GraphQL Mutation: register
     * Yeni kullanıcı kaydı
     */
    @MutationMapping
    public AuthGraphQLResponse register(@Argument("input") RegisterInput input) {
        log.info("GraphQL Mutation: register for email: {}", input.email());

        RegisterRequest request = RegisterRequest.builder()
                .email(input.email())
                .password(input.password())
                .firstName(input.firstName())
                .lastName(input.lastName())
                .build();

        AuthResponse response = authService.register(request);

        return AuthGraphQLResponse.fromAuthResponse(response);
    }

    /**
     * GraphQL Mutation: login
     * Kullanıcı girişi
     */
    @MutationMapping
    public AuthGraphQLResponse login(@Argument("input") LoginInput input) {
        log.info("GraphQL Mutation: login for email: {}", input.email());

        LoginRequest request = new LoginRequest(input.email(), input.password());

        AuthResponse response = authService.login(request);

        return AuthGraphQLResponse.fromAuthResponse(response);
    }

    /**
     * GraphQL Mutation: googleLogin
     * Google ile giriş
     */
    @MutationMapping
    public AuthGraphQLResponse googleLogin(@Argument("input") GoogleLoginInput input) {
        log.info("GraphQL Mutation: googleLogin");

        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .idToken(input.idToken())
                .build();

        AuthResponse response = authService.googleLogin(request);

        return AuthGraphQLResponse.fromAuthResponse(response);
    }

    /**
     * GraphQL Mutation: logout
     * Çıkış
     */
    @MutationMapping
    public Boolean logout(@Argument String userId) {
        log.info("GraphQL Mutation: logout for userId: {}", userId);

        authService.logout(userId);

        return true;
    }

    // ============== Input Records ==============

    public record RegisterInput(
            String email,
            String password,
            String firstName,
            String lastName) {
    }

    public record LoginInput(
            String email,
            String password) {
    }

    public record GoogleLoginInput(
            String idToken) {
    }

    // ============== Response ==============

    public record AuthGraphQLResponse(
            String token,
            String tokenExpiresAt,
            AuthUserResponse user) {

        public static AuthGraphQLResponse fromAuthResponse(AuthResponse response) {
            return new AuthGraphQLResponse(
                    response.getToken(),
                    response.getUser().getTokenExpiresAt() != null
                            ? response.getUser().getTokenExpiresAt().toString()
                            : null,
                    new AuthUserResponse(
                            response.getUser().getId().toString(),
                            response.getUser().getEmail(),
                            response.getUser().getIsGoogleUser()));
        }
    }

    public record AuthUserResponse(
            String id,
            String email,
            Boolean isGoogleUser) {
    }
}
