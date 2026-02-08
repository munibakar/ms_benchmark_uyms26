package com.microservices.content_management_service.service;

import com.microservices.content_management_service.dto.request.CreateSeasonRequest;
import com.microservices.content_management_service.dto.response.SeasonResponse;
import com.microservices.content_management_service.entity.Content;
import com.microservices.content_management_service.entity.Season;
import com.microservices.content_management_service.exception.BadRequestException;
import com.microservices.content_management_service.exception.ResourceNotFoundException;
import com.microservices.content_management_service.repository.ContentRepository;
import com.microservices.content_management_service.repository.SeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Season Service - Content Management Service
 * Sezon yönetimi işlemlerini yönetir
 */
@Service
public class SeasonService {

    private static final Logger log = LoggerFactory.getLogger(SeasonService.class);

    private final SeasonRepository seasonRepository;
    private final ContentRepository contentRepository;

    public SeasonService(SeasonRepository seasonRepository, ContentRepository contentRepository) {
        this.seasonRepository = seasonRepository;
        this.contentRepository = contentRepository;
    }

    /**
     * Yeni sezon oluştur
     */
    @Transactional
    public SeasonResponse createSeason(CreateSeasonRequest request) {
        log.info("Creating season: contentId={}, seasonNumber={}", request.getContentId(), request.getSeasonNumber());

        // Content kontrolü
        Content content = contentRepository.findByIdAndNotDeleted(request.getContentId())
                .orElseThrow(() -> {
                    log.error("Content not found for contentId: {}", request.getContentId());
                    return new ResourceNotFoundException("Content not found for content ID: " + request.getContentId());
                });

        // TV series kontrolü
        if (content.getContentType() != Content.ContentType.TV_SERIES) {
            throw new BadRequestException("Seasons can only be added to TV series");
        }

        // Aynı sezon numarası var mı kontrol et
        seasonRepository.findByContentIdAndSeasonNumber(request.getContentId(), request.getSeasonNumber())
                .ifPresent(existing -> {
                    throw new BadRequestException(
                            String.format("Season %d already exists for content ID: %d",
                                    request.getSeasonNumber(), request.getContentId()));
                });

        Season season = Season.builder()
                .content(content)
                .seasonNumber(request.getSeasonNumber())
                .title(request.getTitle() != null ? request.getTitle() : "Season " + request.getSeasonNumber())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .posterUrl(request.getPosterUrl())
                .isActive(true)
                .build();

        season = seasonRepository.save(season);

        log.info("Season created successfully: seasonId={}, contentId={}, seasonNumber={}",
                season.getId(), request.getContentId(), request.getSeasonNumber());

        return SeasonResponse.fromEntity(season);
    }

    /**
     * Content ID'ye göre aktif sezonları getir
     */
    @Transactional(readOnly = true)
    public List<SeasonResponse> getActiveSeasonsByContentId(Long contentId) {
        log.info("Fetching active seasons for contentId: {}", contentId);

        List<Season> seasons = seasonRepository.findActiveSeasonsByContentId(contentId);

        return seasons.stream()
                .map(SeasonResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Season ID'ye göre sezon getir
     */
    @Transactional(readOnly = true)
    public SeasonResponse getSeasonById(Long seasonId) {
        log.info("Fetching season for seasonId: {}", seasonId);

        Season season = seasonRepository.findByIdAndNotDeleted(seasonId)
                .orElseThrow(() -> {
                    log.error("Season not found for seasonId: {}", seasonId);
                    return new ResourceNotFoundException("Season not found for season ID: " + seasonId);
                });

        return SeasonResponse.fromEntity(season);
    }

    /**
     * Sezon sil (soft delete)
     */
    @Transactional
    public void deleteSeason(Long seasonId) {
        log.info("Deleting season: seasonId={}", seasonId);

        Season season = seasonRepository.findByIdAndNotDeleted(seasonId)
                .orElseThrow(() -> {
                    log.error("Season not found for seasonId: {}", seasonId);
                    return new ResourceNotFoundException("Season not found for season ID: " + seasonId);
                });

        season.setDeletedAt(java.time.LocalDateTime.now());
        season.setIsActive(false);
        seasonRepository.save(season);

        log.info("Season deleted successfully: seasonId={}", seasonId);
    }
}






