package com.microservices.subscription_and_billing_service.repository;

import com.microservices.subscription_and_billing_service.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Subscription Repository
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Kullanıcının aktif aboneliğini bul
     */
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.status = 'ACTIVE' AND s.deletedAt IS NULL")
    Optional<Subscription> findActiveSubscriptionByUserId(@Param("userId") String userId);

    /**
     * Kullanıcının tüm aboneliklerini bul (soft delete edilmemişler)
     */
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Subscription> findAllByUserId(@Param("userId") String userId);

    /**
     * Belirli bir duruma sahip kullanıcı aboneliğini bul
     */
    Optional<Subscription> findByUserIdAndStatus(String userId, Subscription.SubscriptionStatus status);

    /**
     * Süresi dolmak üzere olan abonelikleri bul (otomatik yenileme için)
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.autoRenew = true AND s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsForRenewal(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Süresi dolmuş ama hala aktif olan abonelikleri bul
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < :now")
    List<Subscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);
}




