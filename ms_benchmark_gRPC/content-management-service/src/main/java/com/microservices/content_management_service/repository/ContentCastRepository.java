package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.ContentCast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ContentCast Repository - Content Management Service
 */
@Repository
public interface ContentCastRepository extends JpaRepository<ContentCast, Long> {
    
    /**
     * Content ID'ye göre cast/crew'leri bul
     */
    @Query("SELECT cc FROM ContentCast cc WHERE cc.content.id = :contentId")
    List<ContentCast> findByContentId(@Param("contentId") Long contentId);
    
    /**
     * CastCrew ID'ye göre içerikleri bul
     */
    @Query("SELECT cc FROM ContentCast cc WHERE cc.castCrew.id = :castCrewId")
    List<ContentCast> findByCastCrewId(@Param("castCrewId") Long castCrewId);
    
    /**
     * Content ve CastCrew ID'ye göre ilişkiyi bul
     */
    @Query("SELECT cc FROM ContentCast cc WHERE cc.content.id = :contentId AND cc.castCrew.id = :castCrewId")
    List<ContentCast> findByContentIdAndCastCrewId(@Param("contentId") Long contentId, @Param("castCrewId") Long castCrewId);
    
    /**
     * Content ID'ye göre tüm ilişkileri sil
     */
    @Modifying
    @Query("DELETE FROM ContentCast cc WHERE cc.content.id = :contentId")
    void deleteByContentId(@Param("contentId") Long contentId);
}

