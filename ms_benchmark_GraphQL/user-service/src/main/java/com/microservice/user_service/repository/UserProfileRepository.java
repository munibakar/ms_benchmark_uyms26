package com.microservice.user_service.repository;

import com.microservice.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserProfile Repository - User Service
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Kullanıcı ID'sine göre profil bul
     */
    Optional<UserProfile> findByUserId(String userId);
    
    /**
     * Email'e göre profil bul
     */
    Optional<UserProfile> findByEmail(String email);
    
    /**
     * Kullanıcı ID'sine göre profil var mı kontrol et
     */
    boolean existsByUserId(String userId);
    
    /**
     * Email'e göre profil var mı kontrol et
     */
    boolean existsByEmail(String email);
    
    /**
     * Silinmemiş ve aktif profilleri listele
     */
    Optional<UserProfile> findByUserIdAndDeletedAtIsNullAndIsActive(String userId, Boolean isActive);
}

