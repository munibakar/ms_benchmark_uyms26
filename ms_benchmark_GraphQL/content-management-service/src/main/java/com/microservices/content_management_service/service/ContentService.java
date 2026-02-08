package com.microservices.content_management_service.service;

import com.microservices.content_management_service.dto.request.CreateContentRequest;
import com.microservices.content_management_service.dto.request.UpdateContentRequest;
import com.microservices.content_management_service.dto.response.ContentResponse;
import com.microservices.content_management_service.entity.Content;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import com.microservices.content_management_service.entity.ContentCast;
import com.microservices.content_management_service.entity.ContentGenre;
import com.microservices.content_management_service.exception.BadRequestException;
import com.microservices.content_management_service.exception.ResourceNotFoundException;
import com.microservices.content_management_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Content Service - Content Management Service
 * İçerik yönetimi işlemlerini yönetir
 */
@Service
public class ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentService.class);

    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;
    private final CastCrewRepository castCrewRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentCastRepository contentCastRepository;

    public ContentService(
            ContentRepository contentRepository,
            GenreRepository genreRepository,
            CastCrewRepository castCrewRepository,
            ContentGenreRepository contentGenreRepository,
            ContentCastRepository contentCastRepository) {
        this.contentRepository = contentRepository;
        this.genreRepository = genreRepository;
        this.castCrewRepository = castCrewRepository;
        this.contentGenreRepository = contentGenreRepository;
        this.contentCastRepository = contentCastRepository;
    }

    /**
     * Yeni içerik oluştur
     */
    @Transactional
    public ContentResponse createContent(CreateContentRequest request) {
        log.info("Creating content: title={}, type={}", request.getTitle(), request.getContentType());

        // Content type kontrolü
        if (request.getContentType() == ContentType.TV_SERIES && request.getTotalSeasons() == null) {
            throw new BadRequestException("Total seasons is required for TV series");
        }

        // Content oluştur
        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .releaseYear(request.getReleaseYear())
                .durationMinutes(request.getDurationMinutes())
                .videoFilePath(request.getVideoFilePath())
                .posterUrl(request.getPosterUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .trailerUrl(request.getTrailerUrl())
                .ageRating(request.getAgeRating())
                .language(request.getLanguage() != null ? request.getLanguage() : "tr")
                .status(request.getStatus() != null ? request.getStatus() : ContentStatus.DRAFT)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .totalSeasons(request.getTotalSeasons())
                .viewCount(0L)
                .isActive(true)
                .build();

        content = contentRepository.save(content);

        // Genre ilişkileri ekle
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            final Content finalContent = content; // Lambda için final değişken
            for (Long genreId : request.getGenreIds()) {
                genreRepository.findByIdAndNotDeleted(genreId)
                        .ifPresentOrElse(
                                genre -> {
                                    ContentGenre contentGenre = ContentGenre.builder()
                                            .content(finalContent)
                                            .genre(genre)
                                            .build();
                                    contentGenreRepository.save(contentGenre);
                                },
                                () -> log.warn("Genre not found: {}", genreId));
            }
        }

        // Cast/Crew ilişkileri ekle
        if (request.getCastCrew() != null && !request.getCastCrew().isEmpty()) {
            final Content finalContent = content; // Lambda için final değişken
            for (var castRequest : request.getCastCrew()) {
                castCrewRepository.findByIdAndNotDeleted(castRequest.getCastCrewId())
                        .ifPresentOrElse(
                                castCrew -> {
                                    ContentCast contentCast = ContentCast.builder()
                                            .content(finalContent)
                                            .castCrew(castCrew)
                                            .characterName(castRequest.getCharacterName())
                                            .roleType(castRequest.getRoleType())
                                            .build();
                                    contentCastRepository.save(contentCast);
                                },
                                () -> log.warn("Cast/Crew not found: {}", castRequest.getCastCrewId()));
            }
        }

        log.info("Content created successfully: contentId={}, title={}", content.getId(), content.getTitle());

        // Refresh entity to load relationships
        content = contentRepository.findByIdAndNotDeleted(content.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        return ContentResponse.fromEntity(content);
    }

    /**
     * Önerilen içerikleri getir (Son 10 aktif içerik)
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> getRecommendedContents() {
        log.info("Fetching recommended contents (Top 100)");

        List<Content> contents = contentRepository.findTop100ByIsActiveTrueAndDeletedAtIsNullOrderByCreatedAtDesc();

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Tüm aktif içerikleri getir
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> getAllActiveContents() {
        log.info("Fetching all active contents");

        List<Content> contents = contentRepository.findAllActiveContents();

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Content ID'ye göre içerik getir
     */
    @Transactional(readOnly = true)
    public ContentResponse getContentById(Long contentId) {
        log.info("Fetching content for contentId: {}", contentId);

        Content content = contentRepository.findByIdAndNotDeleted(contentId)
                .orElseThrow(() -> {
                    log.error("Content not found for contentId: {}", contentId);
                    return new ResourceNotFoundException("Content not found for content ID: " + contentId);
                });

        return ContentResponse.fromEntity(content);
    }

    /**
     * Content type'a göre içerikleri getir
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentsByType(ContentType contentType) {
        log.info("Fetching contents for type: {}", contentType);

        List<Content> contents = contentRepository.findByContentTypeAndActive(contentType);

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Status'e göre içerikleri getir
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> getContentsByStatus(ContentStatus status) {
        log.info("Fetching contents for status: {}", status);

        List<Content> contents = contentRepository.findByStatus(status);

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Öne çıkan içerikleri getir
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> getFeaturedContents() {
        log.info("Fetching featured contents");

        List<Content> contents = contentRepository.findFeaturedContents();

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Title'a göre arama
     */
    @Transactional(readOnly = true)
    public List<ContentResponse> searchContentsByTitle(String title) {
        log.info("Searching contents by title: {}", title);

        List<Content> contents = contentRepository.searchByTitle(title);

        return contents.stream()
                .map(ContentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * İçerik güncelle
     */
    @Transactional
    public ContentResponse updateContent(Long contentId, UpdateContentRequest request) {
        log.info("Updating content: contentId={}", contentId);

        Content content = contentRepository.findByIdAndNotDeleted(contentId)
                .orElseThrow(() -> {
                    log.error("Content not found for contentId: {}", contentId);
                    return new ResourceNotFoundException("Content not found for content ID: " + contentId);
                });

        // Güncellemeleri uygula
        if (request.getTitle() != null) {
            content.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            content.setDescription(request.getDescription());
        }
        if (request.getContentType() != null) {
            content.setContentType(request.getContentType());
        }
        if (request.getReleaseYear() != null) {
            content.setReleaseYear(request.getReleaseYear());
        }
        if (request.getDurationMinutes() != null) {
            content.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getVideoFilePath() != null) {
            content.setVideoFilePath(request.getVideoFilePath());
        }
        if (request.getPosterUrl() != null) {
            content.setPosterUrl(request.getPosterUrl());
        }
        if (request.getThumbnailUrl() != null) {
            content.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getTrailerUrl() != null) {
            content.setTrailerUrl(request.getTrailerUrl());
        }
        if (request.getAgeRating() != null) {
            content.setAgeRating(request.getAgeRating());
        }
        if (request.getLanguage() != null) {
            content.setLanguage(request.getLanguage());
        }
        if (request.getStatus() != null) {
            content.setStatus(request.getStatus());
        }
        if (request.getIsFeatured() != null) {
            content.setIsFeatured(request.getIsFeatured());
        }
        if (request.getTotalSeasons() != null) {
            content.setTotalSeasons(request.getTotalSeasons());
        }

        content = contentRepository.save(content);

        // Genre ilişkilerini güncelle
        if (request.getGenreIds() != null) {
            // Mevcut ilişkileri sil
            contentGenreRepository.deleteByContentId(contentId);

            // Yeni ilişkileri ekle
            final Content finalContent = content; // Lambda için final değişken
            for (Long genreId : request.getGenreIds()) {
                genreRepository.findByIdAndNotDeleted(genreId)
                        .ifPresent(genre -> {
                            ContentGenre contentGenre = ContentGenre.builder()
                                    .content(finalContent)
                                    .genre(genre)
                                    .build();
                            contentGenreRepository.save(contentGenre);
                        });
            }
        }

        // Cast/Crew ilişkilerini güncelle
        if (request.getCastCrew() != null) {
            // Mevcut ilişkileri sil
            contentCastRepository.deleteByContentId(contentId);

            // Yeni ilişkileri ekle
            final Content finalContent = content; // Lambda için final değişken
            for (var castRequest : request.getCastCrew()) {
                castCrewRepository.findByIdAndNotDeleted(castRequest.getCastCrewId())
                        .ifPresent(castCrew -> {
                            ContentCast contentCast = ContentCast.builder()
                                    .content(finalContent)
                                    .castCrew(castCrew)
                                    .characterName(castRequest.getCharacterName())
                                    .roleType(castRequest.getRoleType())
                                    .build();
                            contentCastRepository.save(contentCast);
                        });
            }
        }

        log.info("Content updated successfully: contentId={}", contentId);

        // Refresh entity to load relationships
        content = contentRepository.findByIdAndNotDeleted(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        return ContentResponse.fromEntity(content);
    }

    /**
     * İçerik sil (soft delete)
     */
    @Transactional
    public void deleteContent(Long contentId) {
        log.info("Deleting content: contentId={}", contentId);

        Content content = contentRepository.findByIdAndNotDeleted(contentId)
                .orElseThrow(() -> {
                    log.error("Content not found for contentId: {}", contentId);
                    return new ResourceNotFoundException("Content not found for content ID: " + contentId);
                });

        content.setDeletedAt(java.time.LocalDateTime.now());
        content.setIsActive(false);
        contentRepository.save(content);

        log.info("Content deleted successfully: contentId={}", contentId);
    }

    /**
     * İzlenme sayısını artır
     */
    @Transactional
    public void incrementViewCount(Long contentId) {
        log.debug("Incrementing view count for contentId: {}", contentId);

        Content content = contentRepository.findByIdAndNotDeleted(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found for content ID: " + contentId));

        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);
    }
}
