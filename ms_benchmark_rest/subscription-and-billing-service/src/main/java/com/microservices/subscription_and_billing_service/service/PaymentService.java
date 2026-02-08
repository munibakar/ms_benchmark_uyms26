package com.microservices.subscription_and_billing_service.service;

import com.microservices.subscription_and_billing_service.dto.request.AddPaymentMethodRequest;
import com.microservices.subscription_and_billing_service.dto.response.PaymentMethodResponse;
import com.microservices.subscription_and_billing_service.entity.PaymentMethod;
import com.microservices.subscription_and_billing_service.exception.BadRequestException;
import com.microservices.subscription_and_billing_service.exception.ResourceNotFoundException;
import com.microservices.subscription_and_billing_service.repository.PaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Payment Service
 * Ödeme işlemlerini ve ödeme yöntemlerini yönetir
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * Ödeme işlemi yap
     * NOT: Gerçek bir payment gateway entegrasyonu yerine simüle ediyoruz
     */
    @Transactional
    public boolean processPayment(String userId, BigDecimal amount, Long paymentMethodId) {
        log.info("Processing payment for userId: {}, amount: {}", userId, amount);

        // Ödeme yöntemi ID'si verilmişse onu kullan, yoksa default'u kullan
        PaymentMethod paymentMethod;
        if (paymentMethodId != null) {
            paymentMethod = paymentMethodRepository.findByIdAndUserId(paymentMethodId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
        } else {
            paymentMethod = paymentMethodRepository.findDefaultPaymentMethod(userId)
                    .orElseThrow(() -> new BadRequestException(
                            "No payment method found. Please add a payment method first."));
        }

        // Simüle edilmiş ödeme işlemi
        // Gerçek uygulamada burada Stripe, PayPal, iyzico gibi bir gateway'e istek
        // gönderilir
        try {
            // Payment gateway'e istek gönder (simüle)
            boolean paymentSuccess = simulatePaymentGateway(paymentMethod, amount);

            if (paymentSuccess) {
                log.info("Payment successful for userId: {}", userId);
                return true;
            } else {
                log.warn("Payment failed for userId: {}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Payment processing error for userId: {}", userId, e);
            return false;
        }
    }

    /**
     * Ödeme yöntemi ekle
     */
    @Transactional
    public PaymentMethodResponse addPaymentMethod(String userId, AddPaymentMethodRequest request) {
        log.info("Adding payment method for userId: {}", userId);

        // Kart numarasından son 4 haneyi al
        String lastFourDigits = request.getCardNumber().substring(12);

        // Aynı kart bilgilerinin daha önce eklenip eklenmediğini kontrol et
        boolean cardExists = paymentMethodRepository.existsByUserIdAndCardDetails(
                userId,
                lastFourDigits,
                request.getCardBrand(),
                request.getExpiryMonth(),
                request.getExpiryYear());

        if (cardExists) {
            log.warn("Payment method already exists for userId: {} with last four digits: {}", userId, lastFourDigits);
            throw new BadRequestException("Bu kart bilgileri zaten kayıtlı. Aynı kartı tekrar ekleyemezsiniz.");
        }

        // Eğer varsayılan olarak ayarlanacaksa, diğer varsayılan işaretlerini kaldır
        if (request.getSetAsDefault()) {
            paymentMethodRepository.removeAllDefaultFlags(userId);
        }

        // Gerçek uygulamada kart bilgileri payment gateway'e gönderilir ve token alınır
        // Burada simüle ediyoruz
        String paymentToken = "TOKEN_" + System.currentTimeMillis();

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .userId(userId)
                .type("CREDIT_CARD")
                .cardHolderName(request.getCardHolderName())
                .lastFourDigits(lastFourDigits)
                .cardBrand(request.getCardBrand())
                .expiryMonth(request.getExpiryMonth())
                .expiryYear(request.getExpiryYear())
                .paymentToken(paymentToken) // Gerçekte payment gateway'den alınan token
                .isDefault(request.getSetAsDefault())
                .isActive(true)
                .build();

        paymentMethod = paymentMethodRepository.save(paymentMethod);

        log.info("Payment method added successfully for userId: {}", userId);

        return convertToResponse(paymentMethod);
    }

    /**
     * Kullanıcının ödeme yöntemlerini listele
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getPaymentMethods(String userId) {
        log.info("Fetching payment methods for userId: {}", userId);

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findActivePaymentMethodsByUserId(userId);

        return paymentMethods.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ödeme yöntemini sil (soft delete)
     */
    @Transactional
    public void deletePaymentMethod(String userId, Long paymentMethodId) {
        log.info("Deleting payment method {} for userId: {}", paymentMethodId, userId);

        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        paymentMethod.setDeletedAt(LocalDateTime.now());
        paymentMethod.setIsActive(false);

        paymentMethodRepository.save(paymentMethod);

        log.info("Payment method deleted successfully");
    }

    /**
     * Payment gateway simülasyonu
     * Gerçek uygulamada buraya Stripe, PayPal, iyzico gibi entegrasyon gelecek
     */
    private boolean simulatePaymentGateway(PaymentMethod paymentMethod, BigDecimal amount) {
        // %100 başarı oranı ile simüle edilmiş ödeme (test ortamı için)
        // Gerçek uygulamada burada external API çağrısı yapılır
        return true;
    }

    /**
     * Entity'yi Response DTO'ya çevir
     */
    private PaymentMethodResponse convertToResponse(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .type(paymentMethod.getType())
                .cardHolderName(paymentMethod.getCardHolderName())
                .lastFourDigits(paymentMethod.getLastFourDigits())
                .cardBrand(paymentMethod.getCardBrand())
                .expiryMonth(paymentMethod.getExpiryMonth())
                .expiryYear(paymentMethod.getExpiryYear())
                .isDefault(paymentMethod.getIsDefault())
                .isActive(paymentMethod.getIsActive())
                .createdAt(paymentMethod.getCreatedAt())
                .build();
    }
}
