package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Season Repository - Content Management Service
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    
    /**
     * Content ID'ye göre aktif sezonları bul
     */
    @Query("SELECT s FROM Season s WHERE s.content.id = :contentId AND s.deletedAt IS NULL AND s.isActive = true ORDER BY s.seasonNumber ASC")
    List<Season> findActiveSeasonsByContentId(@Param("contentId") Long contentId);
    
    /**
     * Silinmemiş sezonu ID ile bul
     */
    @Query("SELECT s FROM Season s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Season> findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * Content ID ve season number'a göre sezon bul
     */
    @Query("SELECT s FROM Season s WHERE s.content.id = :contentId AND s.seasonNumber = :seasonNumber AND s.deletedAt IS NULL")
    Optional<Season> findByContentIdAndSeasonNumber(@Param("contentId") Long contentId, @Param("seasonNumber") Integer seasonNumber);
}






