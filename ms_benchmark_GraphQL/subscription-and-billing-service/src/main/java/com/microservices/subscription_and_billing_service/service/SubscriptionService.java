package com.microservices.subscription_and_billing_service.service;

import com.microservices.subscription_and_billing_service.dto.request.CancelSubscriptionRequest;
import com.microservices.subscription_and_billing_service.dto.request.SubscribeRequest;
import com.microservices.subscription_and_billing_service.dto.response.SubscriptionPlanResponse;
import com.microservices.subscription_and_billing_service.dto.response.SubscriptionResponse;
import com.microservices.subscription_and_billing_service.entity.BillingHistory;
import com.microservices.subscription_and_billing_service.entity.Subscription;
import com.microservices.subscription_and_billing_service.entity.SubscriptionPlan;
import com.microservices.subscription_and_billing_service.exception.BadRequestException;
import com.microservices.subscription_and_billing_service.exception.PaymentException;
import com.microservices.subscription_and_billing_service.exception.ResourceNotFoundException;
import com.microservices.subscription_and_billing_service.repository.BillingHistoryRepository;
import com.microservices.subscription_and_billing_service.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Subscription Service
 * Kullanıcı aboneliklerinin yönetimini sağlar
 */
@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final BillingHistoryRepository billingHistoryRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final PaymentService paymentService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            BillingHistoryRepository billingHistoryRepository,
            SubscriptionPlanService subscriptionPlanService,
            PaymentService paymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingHistoryRepository = billingHistoryRepository;
        this.subscriptionPlanService = subscriptionPlanService;
        this.paymentService = paymentService;
    }

    /**
     * Kullanıcının aktif aboneliğini getir
     */
    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(String userId) {
        log.info("Fetching active subscription for userId: {}", userId);
        
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user: " + userId));
        
        return convertToResponse(subscription);
    }

    /**
     * Kullanıcının tüm aboneliklerini getir
     */
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAllSubscriptions(String userId) {
        log.info("Fetching all subscriptions for userId: {}", userId);
        
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        
        return subscriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Yeni abonelik oluştur
     */
    @Transactional
    public SubscriptionResponse subscribe(String userId, SubscribeRequest request) {
        log.info("Creating new subscription for userId: {} with plan: {}", userId, request.getPlanName());
        
        // Aktif abonelik var mı kontrol et
        subscriptionRepository.findActiveSubscriptionByUserId(userId)
                .ifPresent(sub -> {
                    throw new BadRequestException("User already has an active subscription");
                });
        
        // Plan bilgisini al
        SubscriptionPlan plan = subscriptionPlanService.getPlanByName(request.getPlanName());
        
        // Billing cycle kontrolü
        Subscription.BillingCycle billingCycle;
        BigDecimal amount;
        try {
            billingCycle = Subscription.BillingCycle.valueOf(request.getBillingCycle().toUpperCase());
            amount = billingCycle == Subscription.BillingCycle.MONTHLY ? 
                    plan.getMonthlyPrice() : plan.getYearlyPrice();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid billing cycle: " + request.getBillingCycle());
        }
        
        // Ödeme işlemi yap
        boolean paymentSuccess = paymentService.processPayment(userId, amount, request.getPaymentMethodId());
        
        if (!paymentSuccess) {
            throw new PaymentException("Payment failed. Please check your payment method.");
        }
        
        // Abonelik oluştur
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = billingCycle == Subscription.BillingCycle.MONTHLY ?
                now.plusMonths(1) : now.plusYears(1);
        
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(plan)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .billingCycle(billingCycle)
                .startDate(now)
                .endDate(endDate)
                .autoRenew(true)
                .nextBillingDate(endDate)
                .lastBillingDate(now)
                .failedPaymentAttempts(0)
                .build();
        
        subscription = subscriptionRepository.save(subscription);
        
        // Fatura kaydı oluştur
        createBillingHistory(subscription, plan, amount, BillingHistory.PaymentStatus.SUCCESS);
        
        log.info("Subscription created successfully for userId: {} with subscriptionId: {}", 
                userId, subscription.getId());
        
        return convertToResponse(subscription);
    }

    /**
     * Aboneliği iptal et
     */
    @Transactional
    public SubscriptionResponse cancelSubscription(String userId, CancelSubscriptionRequest request) {
        log.info("Cancelling subscription for userId: {}", userId);
        
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));
        
        LocalDateTime now = LocalDateTime.now();
        
        if (request.getImmediate()) {
            // Hemen iptal et
            subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
            subscription.setEndDate(now);
        } else {
            // Dönem sonunda iptal et
            subscription.setStatus(Subscription.SubscriptionStatus.CANCELLED);
            subscription.setAutoRenew(false);
        }
        
        subscription.setCancelledAt(now);
        subscription.setCancellationReason(request.getReason());
        
        subscription = subscriptionRepository.save(subscription);
        
        log.info("Subscription cancelled for userId: {}, immediate: {}", userId, request.getImmediate());
        
        return convertToResponse(subscription);
    }

    /**
     * Fatura geçmişi kaydı oluştur
     */
    private void createBillingHistory(Subscription subscription, SubscriptionPlan plan, 
                                     BigDecimal amount, BillingHistory.PaymentStatus status) {
        BillingHistory billing = BillingHistory.builder()
                .userId(subscription.getUserId())
                .subscriptionId(subscription.getId())
                .planId(plan.getId())
                .planName(plan.getDisplayName())
                .amount(amount)
                .currency("TRY")
                .paymentStatus(status)
                .paymentMethod(BillingHistory.PaymentMethod.CREDIT_CARD)
                .transactionId("TXN-" + System.currentTimeMillis())
                .paymentDate(LocalDateTime.now())
                .billingPeriodStart(subscription.getStartDate())
                .billingPeriodEnd(subscription.getEndDate())
                .build();
        
        billingHistoryRepository.save(billing);
    }

    /**
     * Entity'yi Response DTO'ya çevir
     */
    private SubscriptionResponse convertToResponse(Subscription subscription) {
        SubscriptionPlanResponse planResponse = SubscriptionPlanResponse.builder()
                .id(subscription.getPlan().getId())
                .planName(subscription.getPlan().getPlanName())
                .displayName(subscription.getPlan().getDisplayName())
                .description(subscription.getPlan().getDescription())
                .monthlyPrice(subscription.getPlan().getMonthlyPrice())
                .yearlyPrice(subscription.getPlan().getYearlyPrice())
                .maxScreens(subscription.getPlan().getMaxScreens())
                .maxProfiles(subscription.getPlan().getMaxProfiles())
                .videoQuality(subscription.getPlan().getVideoQuality())
                .downloadAvailable(subscription.getPlan().getDownloadAvailable())
                .adsIncluded(subscription.getPlan().getAdsIncluded())
                .isActive(subscription.getPlan().getIsActive())
                .build();
        
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .plan(planResponse)
                .status(subscription.getStatus().name())
                .billingCycle(subscription.getBillingCycle().name())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .cancelledAt(subscription.getCancelledAt())
                .cancellationReason(subscription.getCancellationReason())
                .autoRenew(subscription.getAutoRenew())
                .nextBillingDate(subscription.getNextBillingDate())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}




