package com.microservices.subscription_and_billing_service.entity;

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
 * Subscription Entity
 * Kullanıcıların abonelik bilgilerini tutar
 */
@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_subscription_user_id", columnList = "userId"),
    @Index(name = "idx_subscription_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // Auth service'deki user_id

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status; // ACTIVE, CANCELLED, EXPIRED, SUSPENDED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingCycle billingCycle; // MONTHLY, YEARLY

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime cancelledAt; // İptal tarihi

    @Column
    private String cancellationReason; // İptal nedeni

    @Column(nullable = false)
    private Boolean autoRenew = true; // Otomatik yenileme

    @Column
    private LocalDateTime nextBillingDate; // Bir sonraki faturalama tarihi

    @Column
    private LocalDateTime lastBillingDate; // Son faturalama tarihi

    @Column
    private Integer failedPaymentAttempts = 0; // Başarısız ödeme denemesi

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt; // Soft delete

    public enum SubscriptionStatus {
        ACTIVE,      // Aktif abonelik
        CANCELLED,   // İptal edilmiş (ama hala süre var)
        EXPIRED,     // Süresi dolmuş
        SUSPENDED    // Askıya alınmış (ödeme sorunu vs)
    }

    public enum BillingCycle {
        MONTHLY,
        YEARLY
    }
}


