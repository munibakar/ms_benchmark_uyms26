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
 * PaymentMethod Entity
 * Kullanıcıların kayıtlı ödeme yöntemlerini tutar
 * Güvenlik için gerçek kart bilgileri saklanmaz, sadece payment gateway token'ı saklanır
 */
@Entity
@Table(name = "payment_methods", indexes = {
    @Index(name = "idx_payment_method_user_id", columnList = "userId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String type; // CREDIT_CARD, DEBIT_CARD, PAYPAL

    @Column(nullable = false, length = 100)
    private String cardHolderName;

    @Column(nullable = false, length = 4)
    private String lastFourDigits; // Son 4 hane (örn: "1234")

    @Column(length = 50)
    private String cardBrand; // VISA, MASTERCARD, AMEX

    @Column(nullable = false, length = 2)
    private String expiryMonth;

    @Column(nullable = false, length = 4)
    private String expiryYear;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paymentToken; // Payment gateway'den alınan token (Şifrelenmiş olarak saklanmalı)

    @Column(nullable = false)
    private Boolean isDefault = false; // Varsayılan ödeme yöntemi mi

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt; // Soft delete
}


