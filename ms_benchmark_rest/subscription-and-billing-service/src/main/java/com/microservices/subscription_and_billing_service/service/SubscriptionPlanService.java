package com.microservices.subscription_and_billing_service.service;

import com.microservices.subscription_and_billing_service.dto.response.SubscriptionPlanResponse;
import com.microservices.subscription_and_billing_service.entity.SubscriptionPlan;
import com.microservices.subscription_and_billing_service.exception.ResourceNotFoundException;
import com.microservices.subscription_and_billing_service.repository.SubscriptionPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SubscriptionPlan Service
 * Abonelik planlarının yönetimini sağlar
 */
@Service
public class SubscriptionPlanService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    /**
     * Tüm aktif planları listele
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllActivePlans() {
        log.info("Fetching all active subscription plans");
        
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findByIsActiveTrueOrderBySortOrderAsc();
        
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Plan adına göre plan getir
     */
    @Transactional(readOnly = true)
    public SubscriptionPlan getPlanByName(String planName) {
        return subscriptionPlanRepository.findByPlanName(planName)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found: " + planName));
    }

    /**
     * Plan ID'sine göre plan getir
     */
    @Transactional(readOnly = true)
    public SubscriptionPlan getPlanById(Long planId) {
        return subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + planId));
    }

    /**
     * Entity'yi Response DTO'ya çevir
     */
    private SubscriptionPlanResponse convertToResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .id(plan.getId())
                .planName(plan.getPlanName())
                .displayName(plan.getDisplayName())
                .description(plan.getDescription())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())
                .maxScreens(plan.getMaxScreens())
                .maxProfiles(plan.getMaxProfiles())
                .videoQuality(plan.getVideoQuality())
                .downloadAvailable(plan.getDownloadAvailable())
                .adsIncluded(plan.getAdsIncluded())
                .isActive(plan.getIsActive())
                .build();
    }
}




