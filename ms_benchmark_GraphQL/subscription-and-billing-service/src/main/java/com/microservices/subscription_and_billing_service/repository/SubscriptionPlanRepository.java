package com.microservices.subscription_and_billing_service.repository;

import com.microservices.subscription_and_billing_service.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SubscriptionPlan Repository
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    /**
     * Plan adına göre plan bul
     */
    Optional<SubscriptionPlan> findByPlanName(String planName);

    /**
     * Aktif planları listele
     */
    List<SubscriptionPlan> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * Plan adının var olup olmadığını kontrol et
     */
    boolean existsByPlanName(String planName);
}




