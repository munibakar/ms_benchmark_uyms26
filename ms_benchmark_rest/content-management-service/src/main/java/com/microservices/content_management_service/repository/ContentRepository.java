package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.Content;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Content Repository - Content Management Service
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

       /**
        * Silinmemiş ve aktif içerikleri bul (OOM önlemek için kontrollü fetch)
        */
       @Query("SELECT c FROM Content c WHERE c.deletedAt IS NULL AND c.isActive = true ORDER BY c.createdAt DESC")
       List<Content> findAllActiveContents();

       /**
        * Silinmemiş içeriği ID ile bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.id = :id AND c.deletedAt IS NULL")
       Optional<Content> findByIdAndNotDeleted(@Param("id") Long id);

       /**
        * Content type'a göre aktif içerikleri bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.contentType = :contentType AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.createdAt DESC")
       List<Content> findByContentTypeAndActive(@Param("contentType") ContentType contentType);

       /**
        * Status'e göre içerikleri bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.status = :status AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
       List<Content> findByStatus(@Param("status") ContentStatus status);

       /**
        * Öne çıkan içerikleri bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.isFeatured = true AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.createdAt DESC")
       List<Content> findFeaturedContents();

       /**
        * Title'a göre arama (case-insensitive, partial match) (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.title ASC")
       List<Content> searchByTitle(@Param("title") String title);

       /**
        * Release year'a göre içerikleri bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.releaseYear = :year AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.title ASC")
       List<Content> findByReleaseYear(@Param("year") Integer year);

       /**
        * Status ve Type'a göre içerikleri bul (ilişkilerle birlikte)
        */
       @Query("SELECT DISTINCT c FROM Content c " +
                     "LEFT JOIN FETCH c.contentGenres cg " +
                     "LEFT JOIN FETCH cg.genre " +
                     "LEFT JOIN FETCH c.contentCasts cc " +
                     "LEFT JOIN FETCH cc.castCrew " +
                     "LEFT JOIN FETCH c.seasons s " +
                     "LEFT JOIN FETCH s.episodes " +
                     "WHERE c.status = :status AND c.contentType = :contentType AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.createdAt DESC")
       List<Content> findByStatusAndContentType(@Param("status") ContentStatus status,
                     @Param("contentType") ContentType contentType);
}
