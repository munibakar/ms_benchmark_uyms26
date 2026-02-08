package com.microservices.subscription_and_billing_service.repository;

import com.microservices.subscription_and_billing_service.entity.BillingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BillingHistory Repository
 */
@Repository
public interface BillingHistoryRepository extends JpaRepository<BillingHistory, Long> {

    /**
     * Kullanıcının fatura geçmişini getir (en yeni en üstte)
     */
    List<BillingHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Kullanıcının belirli bir aboneliğe ait faturalarını getir
     */
    List<BillingHistory> findByUserIdAndSubscriptionIdOrderByCreatedAtDesc(String userId, Long subscriptionId);

    /**
     * Başarılı ödemeleri getir
     */
    List<BillingHistory> findByUserIdAndPaymentStatusOrderByCreatedAtDesc(String userId, BillingHistory.PaymentStatus paymentStatus);

    /**
     * Belirli bir tarih aralığındaki faturaları getir
     */
    @Query("SELECT b FROM BillingHistory b WHERE b.userId = :userId AND b.createdAt BETWEEN :startDate AND :endDate ORDER BY b.createdAt DESC")
    List<BillingHistory> findByUserIdAndDateRange(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}




