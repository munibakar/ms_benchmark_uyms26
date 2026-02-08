package com.microservices.subscription_and_billing_service.repository;

import com.microservices.subscription_and_billing_service.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PaymentMethod Repository
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /**
     * Kullanıcının aktif ödeme yöntemlerini getir
     */
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.isActive = true AND pm.deletedAt IS NULL ORDER BY pm.isDefault DESC, pm.createdAt DESC")
    List<PaymentMethod> findActivePaymentMethodsByUserId(@Param("userId") String userId);

    /**
     * Kullanıcının varsayılan ödeme yöntemini getir
     */
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.isDefault = true AND pm.isActive = true AND pm.deletedAt IS NULL")
    Optional<PaymentMethod> findDefaultPaymentMethod(@Param("userId") String userId);

    /**
     * Kullanıcının tüm varsayılan işaretlerini kaldır
     */
    @Modifying
    @Query("UPDATE PaymentMethod pm SET pm.isDefault = false WHERE pm.userId = :userId")
    void removeAllDefaultFlags(@Param("userId") String userId);

    /**
     * ID ve kullanıcı ID'sine göre ödeme yöntemi bul
     */
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.id = :id AND pm.userId = :userId AND pm.deletedAt IS NULL")
    Optional<PaymentMethod> findByIdAndUserId(@Param("id") Long id, @Param("userId") String userId);

    /**
     * Aynı kart bilgilerinin var olup olmadığını kontrol et (userId bazlı)
     * Aynı kullanıcının aynı kartı birden fazla kez eklemesini önler
     */
    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END FROM PaymentMethod pm " +
           "WHERE pm.userId = :userId " +
           "AND pm.lastFourDigits = :lastFourDigits " +
           "AND pm.cardBrand = :cardBrand " +
           "AND pm.expiryMonth = :expiryMonth " +
           "AND pm.expiryYear = :expiryYear " +
           "AND pm.isActive = true " +
           "AND pm.deletedAt IS NULL")
    boolean existsByUserIdAndCardDetails(
            @Param("userId") String userId,
            @Param("lastFourDigits") String lastFourDigits,
            @Param("cardBrand") String cardBrand,
            @Param("expiryMonth") String expiryMonth,
            @Param("expiryYear") String expiryYear
    );
}




