package com.microservices.content_management_service.service;

import com.microservices.content_management_service.dto.request.CreateCastCrewRequest;
import com.microservices.content_management_service.dto.request.UpdateCastCrewRequest;
import com.microservices.content_management_service.dto.response.CastCrewResponse;
import com.microservices.content_management_service.entity.CastCrew;
import com.microservices.content_management_service.exception.ResourceNotFoundException;
import com.microservices.content_management_service.repository.CastCrewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CastCrew Service - Content Management Service
 * Cast/Crew yönetimi işlemlerini yönetir
 */
@Service
public class CastCrewService {

    private static final Logger log = LoggerFactory.getLogger(CastCrewService.class);

    private final CastCrewRepository castCrewRepository;

    public CastCrewService(CastCrewRepository castCrewRepository) {
        this.castCrewRepository = castCrewRepository;
    }

    /**
     * Yeni cast/crew oluştur
     */
    @Transactional
    public CastCrewResponse createCastCrew(CreateCastCrewRequest request) {
        log.info("Creating cast/crew: name={}, roleType={}", request.getName(), request.getRoleType());

        CastCrew castCrew = CastCrew.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .profileImageUrl(request.getProfileImageUrl())
                .roleType(request.getRoleType())
                .dateOfBirth(request.getDateOfBirth())
                .nationality(request.getNationality())
                .isActive(true)
                .build();

        castCrew = castCrewRepository.save(castCrew);

        log.info("Cast/Crew created successfully: castCrewId={}, name={}", castCrew.getId(), castCrew.getName());

        return CastCrewResponse.fromEntity(castCrew);
    }

    /**
     * Tüm aktif cast/crew'leri getir
     */
    @Transactional(readOnly = true)
    public List<CastCrewResponse> getAllActiveCastCrew() {
        log.info("Fetching all active cast/crew");

        List<CastCrew> castCrewList = castCrewRepository.findAllActiveCastCrew();

        return castCrewList.stream()
                .map(CastCrewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Role type'a göre cast/crew'leri getir
     */
    @Transactional(readOnly = true)
    public List<CastCrewResponse> getCastCrewByRoleType(CastCrew.RoleType roleType) {
        log.info("Fetching cast/crew for roleType: {}", roleType);

        List<CastCrew> castCrewList = castCrewRepository.findByRoleType(roleType);

        return castCrewList.stream()
                .map(CastCrewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * CastCrew ID'ye göre cast/crew getir
     */
    @Transactional(readOnly = true)
    public CastCrewResponse getCastCrewById(Long castCrewId) {
        log.info("Fetching cast/crew for castCrewId: {}", castCrewId);

        CastCrew castCrew = castCrewRepository.findByIdAndNotDeleted(castCrewId)
                .orElseThrow(() -> {
                    log.error("Cast/Crew not found for castCrewId: {}", castCrewId);
                    return new ResourceNotFoundException("Cast/Crew not found for cast crew ID: " + castCrewId);
                });

        return CastCrewResponse.fromEntity(castCrew);
    }

    /**
     * İsme göre arama
     */
    @Transactional(readOnly = true)
    public List<CastCrewResponse> searchCastCrewByName(String name) {
        log.info("Searching cast/crew by name: {}", name);

        List<CastCrew> castCrewList = castCrewRepository.searchByName(name);

        return castCrewList.stream()
                .map(CastCrewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cast/crew güncelle
     */
    @Transactional
    public CastCrewResponse updateCastCrew(Long castCrewId, UpdateCastCrewRequest request) {
        log.info("Updating cast/crew: castCrewId={}", castCrewId);

        CastCrew castCrew = castCrewRepository.findByIdAndNotDeleted(castCrewId)
                .orElseThrow(() -> {
                    log.error("Cast/Crew not found for castCrewId: {}", castCrewId);
                    return new ResourceNotFoundException("Cast/Crew not found for cast crew ID: " + castCrewId);
                });

        if (request.getName() != null) {
            castCrew.setName(request.getName());
        }
        if (request.getBiography() != null) {
            castCrew.setBiography(request.getBiography());
        }
        if (request.getProfileImageUrl() != null) {
            castCrew.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getRoleType() != null) {
            castCrew.setRoleType(request.getRoleType());
        }
        if (request.getDateOfBirth() != null) {
            castCrew.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getNationality() != null) {
            castCrew.setNationality(request.getNationality());
        }

        castCrew = castCrewRepository.save(castCrew);

        log.info("Cast/Crew updated successfully: castCrewId={}", castCrewId);

        return CastCrewResponse.fromEntity(castCrew);
    }

    /**
     * Cast/crew sil (soft delete)
     */
    @Transactional
    public void deleteCastCrew(Long castCrewId) {
        log.info("Deleting cast/crew: castCrewId={}", castCrewId);

        CastCrew castCrew = castCrewRepository.findByIdAndNotDeleted(castCrewId)
                .orElseThrow(() -> {
                    log.error("Cast/Crew not found for castCrewId: {}", castCrewId);
                    return new ResourceNotFoundException("Cast/Crew not found for cast crew ID: " + castCrewId);
                });

        castCrew.setDeletedAt(java.time.LocalDateTime.now());
        castCrew.setIsActive(false);
        castCrewRepository.save(castCrew);

        log.info("Cast/Crew deleted successfully: castCrewId={}", castCrewId);
    }
}






