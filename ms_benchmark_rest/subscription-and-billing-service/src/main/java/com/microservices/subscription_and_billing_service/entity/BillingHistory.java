package com.microservices.subscription_and_billing_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BillingHistory Entity
 * Kullanıcıların ödeme ve fatura geçmişini tutar
 */
@Entity
@Table(name = "billing_history", indexes = {
    @Index(name = "idx_billing_user_id", columnList = "userId"),
    @Index(name = "idx_billing_subscription_id", columnList = "subscriptionId"),
    @Index(name = "idx_billing_payment_status", columnList = "paymentStatus")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BillingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long subscriptionId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 100)
    private String planName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Ödenen tutar

    @Column(nullable = false, length = 3)
    private String currency = "TRY"; // Para birimi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 100)
    private String transactionId; // Ödeme gateway'den gelen transaction ID

    @Column
    private LocalDateTime paymentDate;

    @Column
    private LocalDateTime billingPeriodStart;

    @Column
    private LocalDateTime billingPeriodEnd;

    @Column(columnDefinition = "TEXT")
    private String invoiceUrl; // Fatura PDF URL'i

    @Column(columnDefinition = "TEXT")
    private String failureReason; // Ödeme başarısız olduysa neden

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PaymentStatus {
        PENDING,     // Beklemede
        SUCCESS,     // Başarılı
        FAILED,      // Başarısız
        REFUNDED     // İade edilmiş
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
        OTHER
    }
}


