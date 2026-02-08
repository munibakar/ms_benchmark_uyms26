package com.microservices.profile_service.repository;

import com.microservices.profile_service.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Profile Repository - Profile Service
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    /**
     * Account ID'ye göre aktif profilleri bul
     */
    @Query("SELECT p FROM Profile p WHERE p.accountId = :accountId AND p.deletedAt IS NULL AND p.isActive = true ORDER BY p.isDefault DESC, p.createdAt ASC")
    List<Profile> findActiveProfilesByAccountId(@Param("accountId") String accountId);
    
    /**
     * Account ID'ye göre tüm profilleri bul (silinmemişler)
     */
    @Query("SELECT p FROM Profile p WHERE p.accountId = :accountId AND p.deletedAt IS NULL ORDER BY p.isDefault DESC, p.createdAt ASC")
    List<Profile> findAllByAccountId(@Param("accountId") String accountId);
    
    /**
     * Account ID'ye göre profil sayısını bul (aktif ve silinmemiş)
     */
    @Query("SELECT COUNT(p) FROM Profile p WHERE p.accountId = :accountId AND p.deletedAt IS NULL AND p.isActive = true")
    long countActiveProfilesByAccountId(@Param("accountId") String accountId);
    
    /**
     * Profile ID'ye göre profil bul (silinmemiş)
     */
    @Query("SELECT p FROM Profile p WHERE p.id = :profileId AND p.deletedAt IS NULL")
    Optional<Profile> findByIdAndNotDeleted(@Param("profileId") Long profileId);
    
    /**
     * Account ID ve Profile ID'ye göre profil bul (silinmemiş)
     */
    @Query("SELECT p FROM Profile p WHERE p.id = :profileId AND p.accountId = :accountId AND p.deletedAt IS NULL")
    Optional<Profile> findByIdAndAccountId(@Param("profileId") Long profileId, @Param("accountId") String accountId);
    
    /**
     * Account ID'ye göre varsayılan profili bul
     */
    @Query("SELECT p FROM Profile p WHERE p.accountId = :accountId AND p.isDefault = true AND p.deletedAt IS NULL AND p.isActive = true")
    Optional<Profile> findDefaultProfileByAccountId(@Param("accountId") String accountId);
    
    /**
     * Account ID'ye göre profil var mı kontrol et (aktif ve silinmemiş)
     */
    @Query("SELECT COUNT(p) > 0 FROM Profile p WHERE p.accountId = :accountId AND p.deletedAt IS NULL AND p.isActive = true")
    boolean existsByAccountId(@Param("accountId") String accountId);
}
