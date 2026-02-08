package com.authentication.microservices.authentication.service;

import com.authentication.microservices.authentication.client.UserServiceClient;
import com.authentication.microservices.authentication.dto.request.CreateUserProfileRequest;
import com.authentication.microservices.authentication.dto.request.GoogleLoginRequest;
import com.authentication.microservices.authentication.dto.request.LoginRequest;
import com.authentication.microservices.authentication.dto.request.RegisterRequest;
import com.authentication.microservices.authentication.dto.response.AuthResponse;
import com.authentication.microservices.authentication.dto.response.UserProfileResponse;
import com.authentication.microservices.authentication.entity.User;
import com.authentication.microservices.authentication.exception.BadRequestException;
import com.authentication.microservices.authentication.repository.UserRepository;
import com.authentication.microservices.authentication.security.JwtTokenProvider;
import com.authentication.microservices.authentication.util.GoogleAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * Authentication Service - Authentication Microservice
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleAuthUtil googleAuthUtil;
    private final UserServiceClient userServiceClient;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       GoogleAuthUtil googleAuthUtil,
                       UserServiceClient userServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.googleAuthUtil = googleAuthUtil;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Register new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Generate new user ID
        String nextUserId = UUID.randomUUID().toString();

        // Create new user
        User user = User.builder()
                .userId(nextUserId)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isGoogleUser(false)
                .build();

        user = userRepository.save(user);

        // OpenFeign ile User Service'e kullanıcı profili oluşturma isteği gönder
        try {
            CreateUserProfileRequest userProfileRequest = CreateUserProfileRequest.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build();
            
            UserProfileResponse userProfile = userServiceClient.createUserProfile(userProfileRequest);
            log.info("User profile created successfully in User Service for userId: {}", userProfile.getUserId());
        } catch (Exception e) {
            log.error("Failed to create user profile in User Service for userId: {}", user.getUserId(), e);
            // Eğer User Service'te profil oluşturulamazsa, kullanıcıyı Auth DB'den de sil
            userRepository.delete(user);
            throw new BadRequestException("Failed to create user profile: " + e.getMessage());
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
        
        // Update user with token info
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
        user.setActiveToken(token);
        user.setTokenExpiresAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        user = userRepository.save(user);

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    /**
     * Login user
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Check if user has a valid active token
        String token = user.getActiveToken();
        if (token != null && user.getTokenExpiresAt() != null) {
            // Check if token is still valid
            if (user.getTokenExpiresAt().isAfter(LocalDateTime.now()) && 
                jwtTokenProvider.validateToken(token)) {
                log.info("User logged in with existing token: {}", user.getEmail());
                return AuthResponse.builder()
                        .token(token)
                        .user(user)
                        .build();
            }
        }

        // Generate new JWT token
        token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
        
        // Update user with new token info
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
        user.setActiveToken(token);
        user.setTokenExpiresAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        userRepository.save(user);

        log.info("User logged in with new token: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    /**
     * Google Login
     */
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        // Verify Google token
        GoogleAuthUtil.GoogleUserInfo googleUser = googleAuthUtil.verifyGoogleToken(request.getIdToken());

        // Check if user exists
        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    // Create new user
                    String nextUserId = UUID.randomUUID().toString();

                    User newUser = User.builder()
                            .userId(nextUserId)
                            .email(googleUser.getEmail())
                            .isGoogleUser(true)
                            .build();

                    newUser = userRepository.save(newUser);

                    // OpenFeign ile User Service'e kullanıcı profili oluşturma isteği gönder
                    try {
                        CreateUserProfileRequest userProfileRequest = CreateUserProfileRequest.builder()
                                .userId(newUser.getUserId())
                                .email(newUser.getEmail())
                                .firstName(googleUser.getGivenName())
                                .lastName(googleUser.getFamilyName())
                                .build();
                        
                        UserProfileResponse userProfile = userServiceClient.createUserProfile(userProfileRequest);
                        log.info("User profile created successfully in User Service for Google user userId: {}", userProfile.getUserId());
                    } catch (Exception e) {
                        log.error("Failed to create user profile in User Service for Google user userId: {}", newUser.getUserId(), e);
                        // Eğer User Service'te profil oluşturulamazsa, kullanıcıyı Auth DB'den de sil
                        userRepository.delete(newUser);
                        throw new BadRequestException("Failed to create user profile: " + e.getMessage());
                    }

                    log.info("New Google user created: {}", newUser.getEmail());
                    return newUser;
                });

        // Check if user has a valid active token
        String token = user.getActiveToken();
        if (token != null && user.getTokenExpiresAt() != null) {
            // Check if token is still valid
            if (user.getTokenExpiresAt().isAfter(LocalDateTime.now()) && 
                jwtTokenProvider.validateToken(token)) {
                log.info("User logged in with Google using existing token: {}", user.getEmail());
                return AuthResponse.builder()
                        .token(token)
                        .user(user)
                        .build();
            }
        }

        // Generate new JWT token
        token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
        
        // Update user with new token info
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
        user.setActiveToken(token);
        user.setTokenExpiresAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        user = userRepository.save(user);

        log.info("User logged in with Google using new token: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }

    /**
     * Logout user - invalidate current token
     */
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        // Clear active token
        user.setActiveToken(null);
        user.setTokenExpiresAt(null);
        userRepository.save(user);
        
        log.info("User logged out successfully: {}", user.getEmail());
    }
}

