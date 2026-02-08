package com.microservices.subscription_and_billing_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SubscriptionPlan Entity
 * Netflix abonelik paketlerini temsil eder (Basic, Standard, Premium)
 */
@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String planName; // FREE, BASIC, STANDARD, PREMIUM

    @Column(nullable = false, length = 100)
    private String displayName; // "Temel Plan", "Standart Plan", "Premium Plan"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal yearlyPrice;

    // Plan Özellikleri
    @Column(nullable = false)
    private Integer maxScreens = 1; // Aynı anda kaç ekranda izlenebilir

    @Column(nullable = false)
    private Integer maxProfiles = 1; // Maksimum profil sayısı

    @Column(nullable = false)
    private String videoQuality; // SD, HD, 4K

    @Column(nullable = false)
    private Boolean downloadAvailable = false; // İndirme özelliği var mı

    @Column(nullable = false)
    private Boolean adsIncluded = true; // Reklam var mı

    @Column(nullable = false)
    private Boolean isActive = true; // Plan aktif mi

    @Column(nullable = false)
    private Integer sortOrder = 0; // Listeleme sırası

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}




