package com.microservices.profile_service.entity;

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
 * Profile Entity - Profile Service
 * Bu entity bir ana hesaba (accountId) bağlı profilleri tutar
 * Netflix benzeri profil yönetimi için kullanılır
 */
@Entity
@Table(name = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private String accountId; // User Service'teki userId'ye referans

    @Column(name = "profile_name", nullable = false, length = 50)
    private String profileName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "is_child_profile")
    @Builder.Default
    private Boolean isChildProfile = false;

    @Column(name = "maturity_level", length = 20)
    @Builder.Default
    private String maturityLevel = "ALL"; // ALL, PG, PG13, R, NC17

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "tr"; // tr, en, fr, de, etc.

    @Column(name = "is_pin_protected")
    @Builder.Default
    private Boolean isPinProtected = false;

    @Column(name = "pin_hash", length = 255)
    private String pinHash; // PIN şifrelenmiş hali

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false; // Varsayılan profil mi?

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
