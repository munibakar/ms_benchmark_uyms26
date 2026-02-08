package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Genre Repository - Content Management Service
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    /**
     * Silinmemiş ve aktif türleri bul
     */
    @Query("SELECT g FROM Genre g WHERE g.deletedAt IS NULL AND g.isActive = true ORDER BY g.name ASC")
    List<Genre> findAllActiveGenres();
    
    /**
     * Silinmemiş türü ID ile bul
     */
    @Query("SELECT g FROM Genre g WHERE g.id = :id AND g.deletedAt IS NULL")
    Optional<Genre> findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * İsme göre tür bul (case-insensitive)
     */
    @Query("SELECT g FROM Genre g WHERE LOWER(g.name) = LOWER(:name) AND g.deletedAt IS NULL")
    Optional<Genre> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * İsme göre arama (case-insensitive, partial match)
     */
    @Query("SELECT g FROM Genre g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%')) AND g.deletedAt IS NULL AND g.isActive = true ORDER BY g.name ASC")
    List<Genre> searchByName(@Param("name") String name);
}






