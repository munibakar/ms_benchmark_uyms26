package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.ContentGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ContentGenre Repository - Content Management Service
 */
@Repository
public interface ContentGenreRepository extends JpaRepository<ContentGenre, Long> {
    
    /**
     * Content ID'ye göre türleri bul
     */
    @Query("SELECT cg FROM ContentGenre cg WHERE cg.content.id = :contentId")
    List<ContentGenre> findByContentId(@Param("contentId") Long contentId);
    
    /**
     * Genre ID'ye göre içerikleri bul
     */
    @Query("SELECT cg FROM ContentGenre cg WHERE cg.genre.id = :genreId")
    List<ContentGenre> findByGenreId(@Param("genreId") Long genreId);
    
    /**
     * Content ve Genre ID'ye göre ilişkiyi bul
     */
    @Query("SELECT cg FROM ContentGenre cg WHERE cg.content.id = :contentId AND cg.genre.id = :genreId")
    List<ContentGenre> findByContentIdAndGenreId(@Param("contentId") Long contentId, @Param("genreId") Long genreId);
    
    /**
     * Content ID'ye göre tüm ilişkileri sil
     */
    @Modifying
    @Query("DELETE FROM ContentGenre cg WHERE cg.content.id = :contentId")
    void deleteByContentId(@Param("contentId") Long contentId);
}

