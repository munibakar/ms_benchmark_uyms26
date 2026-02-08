package com.microservices.content_management_service.controller;

import com.microservices.content_management_service.dto.response.*;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import com.microservices.content_management_service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Content Management Service - GraphQL Controller
 * Tüm GraphQL query'lerini handle eder
 */
@Controller
public class ContentGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(ContentGraphQLController.class);

    private final ContentService contentService;
    private final GenreService genreService;
    private final CastCrewService castCrewService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;

    public ContentGraphQLController(ContentService contentService,
            GenreService genreService,
            CastCrewService castCrewService,
            SeasonService seasonService,
            EpisodeService episodeService) {
        this.contentService = contentService;
        this.genreService = genreService;
        this.castCrewService = castCrewService;
        this.seasonService = seasonService;
        this.episodeService = episodeService;
    }

    // ============== Content Queries ==============

    /**
     * GraphQL Query: getAllActiveContents
     * Tüm aktif içerikleri getir
     */
    @QueryMapping
    public List<ContentResponse> getAllActiveContents() {
        log.info("GraphQL Query: getAllActiveContents");
        return contentService.getAllActiveContents();
    }

    /**
     * GraphQL Query: getContentById
     * Content ID'ye göre içerik getir
     */
    @QueryMapping
    public ContentResponse getContentById(@Argument Long contentId) {
        log.info("GraphQL Query: getContentById for contentId: {}", contentId);
        return contentService.getContentById(contentId);
    }

    /**
     * GraphQL Query: getContentsByType
     * Content type'a göre içerikleri getir
     */
    @QueryMapping
    public List<ContentResponse> getContentsByType(@Argument ContentType contentType) {
        log.info("GraphQL Query: getContentsByType for type: {}", contentType);
        return contentService.getContentsByType(contentType);
    }

    /**
     * GraphQL Query: getContentsByStatus
     * Status'e göre içerikleri getir
     */
    @QueryMapping
    public List<ContentResponse> getContentsByStatus(@Argument ContentStatus status) {
        log.info("GraphQL Query: getContentsByStatus for status: {}", status);
        return contentService.getContentsByStatus(status);
    }

    /**
     * GraphQL Query: getFeaturedContents
     * Öne çıkan içerikleri getir
     */
    @QueryMapping
    public List<ContentResponse> getFeaturedContents() {
        log.info("GraphQL Query: getFeaturedContents");
        return contentService.getFeaturedContents();
    }

    /**
     * GraphQL Query: searchContentsByTitle
     * Title'a göre arama
     */
    @QueryMapping
    public List<ContentResponse> searchContentsByTitle(@Argument String title) {
        log.info("GraphQL Query: searchContentsByTitle for title: {}", title);
        return contentService.searchContentsByTitle(title);
    }

    // ============== Genre Queries ==============

    /**
     * GraphQL Query: getAllActiveGenres
     * Tüm aktif türleri getir
     */
    @QueryMapping
    public List<GenreResponse> getAllActiveGenres() {
        log.info("GraphQL Query: getAllActiveGenres");
        return genreService.getAllActiveGenres();
    }

    /**
     * GraphQL Query: getGenreById
     * Genre ID'ye göre tür getir
     */
    @QueryMapping
    public GenreResponse getGenreById(@Argument Long genreId) {
        log.info("GraphQL Query: getGenreById for genreId: {}", genreId);
        return genreService.getGenreById(genreId);
    }

    /**
     * GraphQL Query: searchGenresByName
     * İsme göre tür arama
     */
    @QueryMapping
    public List<GenreResponse> searchGenresByName(@Argument String name) {
        log.info("GraphQL Query: searchGenresByName for name: {}", name);
        return genreService.searchGenresByName(name);
    }

    // ============== CastCrew Queries ==============

    /**
     * GraphQL Query: getAllActiveCastCrew
     * Tüm aktif cast/crew'leri getir
     */
    @QueryMapping
    public List<CastCrewResponse> getAllActiveCastCrew() {
        log.info("GraphQL Query: getAllActiveCastCrew");
        return castCrewService.getAllActiveCastCrew();
    }

    /**
     * GraphQL Query: getCastCrewById
     * CastCrew ID'ye göre cast/crew getir
     */
    @QueryMapping
    public CastCrewResponse getCastCrewById(@Argument Long castCrewId) {
        log.info("GraphQL Query: getCastCrewById for castCrewId: {}", castCrewId);
        return castCrewService.getCastCrewById(castCrewId);
    }

    /**
     * GraphQL Query: getCastCrewByRoleType
     * Role type'a göre cast/crew'leri getir
     */
    @QueryMapping
    public List<CastCrewResponse> getCastCrewByRoleType(@Argument RoleType roleType) {
        log.info("GraphQL Query: getCastCrewByRoleType for roleType: {}", roleType);
        return castCrewService.getCastCrewByRoleType(roleType);
    }

    /**
     * GraphQL Query: searchCastCrewByName
     * İsme göre cast/crew arama
     */
    @QueryMapping
    public List<CastCrewResponse> searchCastCrewByName(@Argument String name) {
        log.info("GraphQL Query: searchCastCrewByName for name: {}", name);
        return castCrewService.searchCastCrewByName(name);
    }

    // ============== Season Queries ==============

    /**
     * GraphQL Query: getActiveSeasonsByContentId
     * Content ID'ye göre aktif sezonları getir
     */
    @QueryMapping
    public List<SeasonResponse> getActiveSeasonsByContentId(@Argument Long contentId) {
        log.info("GraphQL Query: getActiveSeasonsByContentId for contentId: {}", contentId);
        return seasonService.getActiveSeasonsByContentId(contentId);
    }

    /**
     * GraphQL Query: getSeasonById
     * Season ID'ye göre sezon getir
     */
    @QueryMapping
    public SeasonResponse getSeasonById(@Argument Long seasonId) {
        log.info("GraphQL Query: getSeasonById for seasonId: {}", seasonId);
        return seasonService.getSeasonById(seasonId);
    }

    // ============== Episode Queries ==============

    /**
     * GraphQL Query: getActiveEpisodesBySeasonId
     * Season ID'ye göre aktif bölümleri getir
     */
    @QueryMapping
    public List<EpisodeResponse> getActiveEpisodesBySeasonId(@Argument Long seasonId) {
        log.info("GraphQL Query: getActiveEpisodesBySeasonId for seasonId: {}", seasonId);
        return episodeService.getActiveEpisodesBySeasonId(seasonId);
    }

    /**
     * GraphQL Query: getEpisodeById
     * Episode ID'ye göre bölüm getir
     */
    @QueryMapping
    public EpisodeResponse getEpisodeById(@Argument Long episodeId) {
        log.info("GraphQL Query: getEpisodeById for episodeId: {}", episodeId);
        return episodeService.getEpisodeById(episodeId);
    }
    // ============== Federation Resolvers ==============

    /**
     * Federation Resolver: User.recommendedContents
     * Basit bir implementasyon: Tüm içeriklerden ilk 10 tanesini önerir
     */
    @org.springframework.graphql.data.method.annotation.SchemaMapping(typeName = "User")
    public List<ContentResponse> recommendedContents(User user) {
        log.info("Federation Resolver: Resolving recommendedContents for userId: {}", user.userId());
        return contentService.getRecommendedContents();
    }

    /**
     * Federation User Entity Stub
     */
    public record User(String userId) {
    }
}
