package com.microservices.content_management_service.service;

import com.microservices.content_management_service.dto.request.CreateGenreRequest;
import com.microservices.content_management_service.dto.request.UpdateGenreRequest;
import com.microservices.content_management_service.dto.response.GenreResponse;
import com.microservices.content_management_service.entity.Genre;
import com.microservices.content_management_service.exception.BadRequestException;
import com.microservices.content_management_service.exception.ResourceNotFoundException;
import com.microservices.content_management_service.repository.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Genre Service - Content Management Service
 * Tür yönetimi işlemlerini yönetir
 */
@Service
public class GenreService {

    private static final Logger log = LoggerFactory.getLogger(GenreService.class);

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Yeni tür oluştur
     */
    @Transactional
    public GenreResponse createGenre(CreateGenreRequest request) {
        log.info("Creating genre: name={}", request.getName());

        // Aynı isimde tür var mı kontrol et
        genreRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    throw new BadRequestException("Genre with name '" + request.getName() + "' already exists");
                });

        Genre genre = Genre.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();

        genre = genreRepository.save(genre);

        log.info("Genre created successfully: genreId={}, name={}", genre.getId(), genre.getName());

        return GenreResponse.fromEntity(genre);
    }

    /**
     * Tüm aktif türleri getir
     */
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllActiveGenres() {
        log.info("Fetching all active genres");

        List<Genre> genres = genreRepository.findAllActiveGenres();

        return genres.stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Genre ID'ye göre tür getir
     */
    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long genreId) {
        log.info("Fetching genre for genreId: {}", genreId);

        Genre genre = genreRepository.findByIdAndNotDeleted(genreId)
                .orElseThrow(() -> {
                    log.error("Genre not found for genreId: {}", genreId);
                    return new ResourceNotFoundException("Genre not found for genre ID: " + genreId);
                });

        return GenreResponse.fromEntity(genre);
    }

    /**
     * İsme göre arama
     */
    @Transactional(readOnly = true)
    public List<GenreResponse> searchGenresByName(String name) {
        log.info("Searching genres by name: {}", name);

        List<Genre> genres = genreRepository.searchByName(name);

        return genres.stream()
                .map(GenreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Tür güncelle
     */
    @Transactional
    public GenreResponse updateGenre(Long genreId, UpdateGenreRequest request) {
        log.info("Updating genre: genreId={}", genreId);

        Genre genre = genreRepository.findByIdAndNotDeleted(genreId)
                .orElseThrow(() -> {
                    log.error("Genre not found for genreId: {}", genreId);
                    return new ResourceNotFoundException("Genre not found for genre ID: " + genreId);
                });

        // İsim değişiyorsa kontrol et
        if (request.getName() != null && !request.getName().equalsIgnoreCase(genre.getName())) {
            genreRepository.findByNameIgnoreCase(request.getName())
                    .ifPresent(existing -> {
                        throw new BadRequestException("Genre with name '" + request.getName() + "' already exists");
                    });
            genre.setName(request.getName());
        }

        if (request.getDescription() != null) {
            genre.setDescription(request.getDescription());
        }

        genre = genreRepository.save(genre);

        log.info("Genre updated successfully: genreId={}", genreId);

        return GenreResponse.fromEntity(genre);
    }

    /**
     * Tür sil (soft delete)
     */
    @Transactional
    public void deleteGenre(Long genreId) {
        log.info("Deleting genre: genreId={}", genreId);

        Genre genre = genreRepository.findByIdAndNotDeleted(genreId)
                .orElseThrow(() -> {
                    log.error("Genre not found for genreId: {}", genreId);
                    return new ResourceNotFoundException("Genre not found for genre ID: " + genreId);
                });

        genre.setDeletedAt(java.time.LocalDateTime.now());
        genre.setIsActive(false);
        genreRepository.save(genre);

        log.info("Genre deleted successfully: genreId={}", genreId);
    }
}






