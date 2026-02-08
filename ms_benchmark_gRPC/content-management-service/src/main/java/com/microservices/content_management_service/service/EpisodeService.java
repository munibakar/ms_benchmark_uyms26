package com.microservices.content_management_service.service;

import com.microservices.content_management_service.dto.request.CreateEpisodeRequest;
import com.microservices.content_management_service.dto.response.EpisodeResponse;
import com.microservices.content_management_service.entity.Episode;
import com.microservices.content_management_service.entity.Season;
import com.microservices.content_management_service.exception.BadRequestException;
import com.microservices.content_management_service.exception.ResourceNotFoundException;
import com.microservices.content_management_service.repository.EpisodeRepository;
import com.microservices.content_management_service.repository.SeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Episode Service - Content Management Service
 * Bölüm yönetimi işlemlerini yönetir
 */
@Service
public class EpisodeService {

    private static final Logger log = LoggerFactory.getLogger(EpisodeService.class);

    private final EpisodeRepository episodeRepository;
    private final SeasonRepository seasonRepository;

    public EpisodeService(EpisodeRepository episodeRepository, SeasonRepository seasonRepository) {
        this.episodeRepository = episodeRepository;
        this.seasonRepository = seasonRepository;
    }

    /**
     * Yeni bölüm oluştur
     */
    @Transactional
    public EpisodeResponse createEpisode(CreateEpisodeRequest request) {
        log.info("Creating episode: seasonId={}, episodeNumber={}", request.getSeasonId(), request.getEpisodeNumber());

        // Season kontrolü
        Season season = seasonRepository.findByIdAndNotDeleted(request.getSeasonId())
                .orElseThrow(() -> {
                    log.error("Season not found for seasonId: {}", request.getSeasonId());
                    return new ResourceNotFoundException("Season not found for season ID: " + request.getSeasonId());
                });

        // Aynı bölüm numarası var mı kontrol et
        episodeRepository.findBySeasonIdAndEpisodeNumber(request.getSeasonId(), request.getEpisodeNumber())
                .ifPresent(existing -> {
                    throw new BadRequestException(
                            String.format("Episode %d already exists for season ID: %d",
                                    request.getEpisodeNumber(), request.getSeasonId()));
                });

        Episode episode = Episode.builder()
                .season(season)
                .episodeNumber(request.getEpisodeNumber())
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .videoFilePath(request.getVideoFilePath())
                .thumbnailUrl(request.getThumbnailUrl())
                .releaseDate(request.getReleaseDate())
                .isActive(true)
                .build();

        episode = episodeRepository.save(episode);

        log.info("Episode created successfully: episodeId={}, seasonId={}, episodeNumber={}",
                episode.getId(), request.getSeasonId(), request.getEpisodeNumber());

        return EpisodeResponse.fromEntity(episode);
    }

    /**
     * Season ID'ye göre aktif bölümleri getir
     */
    @Transactional(readOnly = true)
    public List<EpisodeResponse> getActiveEpisodesBySeasonId(Long seasonId) {
        log.info("Fetching active episodes for seasonId: {}", seasonId);

        List<Episode> episodes = episodeRepository.findActiveEpisodesBySeasonId(seasonId);

        return episodes.stream()
                .map(EpisodeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Episode ID'ye göre bölüm getir
     */
    @Transactional(readOnly = true)
    public EpisodeResponse getEpisodeById(Long episodeId) {
        log.info("Fetching episode for episodeId: {}", episodeId);

        Episode episode = episodeRepository.findByIdAndNotDeleted(episodeId)
                .orElseThrow(() -> {
                    log.error("Episode not found for episodeId: {}", episodeId);
                    return new ResourceNotFoundException("Episode not found for episode ID: " + episodeId);
                });

        return EpisodeResponse.fromEntity(episode);
    }

    /**
     * Bölüm sil (soft delete)
     */
    @Transactional
    public void deleteEpisode(Long episodeId) {
        log.info("Deleting episode: episodeId={}", episodeId);

        Episode episode = episodeRepository.findByIdAndNotDeleted(episodeId)
                .orElseThrow(() -> {
                    log.error("Episode not found for episodeId: {}", episodeId);
                    return new ResourceNotFoundException("Episode not found for episode ID: " + episodeId);
                });

        episode.setDeletedAt(java.time.LocalDateTime.now());
        episode.setIsActive(false);
        episodeRepository.save(episode);

        log.info("Episode deleted successfully: episodeId={}", episodeId);
    }
}






