package com.microservices.content_management_service.entity;

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
 * CastCrew Entity - Content Management Service
 * Oyuncular ve ekip üyelerini temsil eder
 */
@Entity
@Table(name = "cast_crew")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CastCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(name = "role_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType roleType; // ACTOR, DIRECTOR, WRITER, PRODUCER

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete

    public enum RoleType {
        ACTOR,
        DIRECTOR,
        WRITER,
        PRODUCER
    }
}






