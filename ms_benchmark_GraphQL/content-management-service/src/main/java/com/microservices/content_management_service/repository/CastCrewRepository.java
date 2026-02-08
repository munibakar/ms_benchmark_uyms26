package com.microservices.content_management_service.repository;

import com.microservices.content_management_service.entity.CastCrew;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CastCrew Repository - Content Management Service
 */
@Repository
public interface CastCrewRepository extends JpaRepository<CastCrew, Long> {
    
    /**
     * Silinmemiş ve aktif cast/crew'leri bul
     */
    @Query("SELECT c FROM CastCrew c WHERE c.deletedAt IS NULL AND c.isActive = true ORDER BY c.name ASC")
    List<CastCrew> findAllActiveCastCrew();
    
    /**
     * Silinmemiş cast/crew'ü ID ile bul
     */
    @Query("SELECT c FROM CastCrew c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<CastCrew> findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * Role type'a göre cast/crew'leri bul
     */
    @Query("SELECT c FROM CastCrew c WHERE c.roleType = :roleType AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.name ASC")
    List<CastCrew> findByRoleType(@Param("roleType") RoleType roleType);
    
    /**
     * İsme göre arama (case-insensitive, partial match)
     */
    @Query("SELECT c FROM CastCrew c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.deletedAt IS NULL AND c.isActive = true ORDER BY c.name ASC")
    List<CastCrew> searchByName(@Param("name") String name);
}






