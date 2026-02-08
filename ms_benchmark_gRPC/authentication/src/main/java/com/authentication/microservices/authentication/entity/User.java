package com.authentication.microservices.authentication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * User Entity - Authentication Microservice
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "is_google_user")
    @Builder.Default
    private Boolean isGoogleUser = false;

    @JsonIgnore
    @Column(name = "active_token", columnDefinition = "TEXT")
    private String activeToken;

    @JsonIgnore
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

