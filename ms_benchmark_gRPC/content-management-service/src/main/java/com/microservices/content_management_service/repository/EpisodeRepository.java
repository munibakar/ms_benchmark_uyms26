package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Episode Repository - Content Management Service
 */
@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    
    /**
     * Season ID'ye göre aktif bölümleri bul
     */
    @Query("SELECT e FROM Episode e WHERE e.season.id = :seasonId AND e.deletedAt IS NULL AND e.isActive = true ORDER BY e.episodeNumber ASC")
    List<Episode> findActiveEpisodesBySeasonId(@Param("seasonId") Long seasonId);
    
    /**
     * Silinmemiş bölümü ID ile bul
     */
    @Query("SELECT e FROM Episode e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Episode> findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * Season ID ve episode number'a göre bölüm bul
     */
    @Query("SELECT e FROM Episode e WHERE e.season.id = :seasonId AND e.episodeNumber = :episodeNumber AND e.deletedAt IS NULL")
    Optional<Episode> findBySeasonIdAndEpisodeNumber(@Param("seasonId") Long seasonId, @Param("episodeNumber") Integer episodeNumber);
}






