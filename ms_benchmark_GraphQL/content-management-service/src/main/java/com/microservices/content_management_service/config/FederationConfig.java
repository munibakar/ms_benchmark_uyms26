package com.microservices.content_management_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.microservices.content_management_service.controller.ContentGraphQLController;
import com.microservices.content_management_service.dto.response.ContentResponse;
import com.microservices.content_management_service.dto.response.EpisodeResponse;
import com.microservices.content_management_service.dto.response.GenreResponse;
import com.microservices.content_management_service.dto.response.SeasonResponse;
import com.microservices.content_management_service.dto.response.CastCrewResponse;
import com.microservices.content_management_service.service.ContentService;
import com.microservices.content_management_service.service.EpisodeService;
import com.microservices.content_management_service.service.GenreService;
import com.microservices.content_management_service.service.SeasonService;
import com.microservices.content_management_service.service.CastCrewService;
import graphql.schema.DataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Apollo Federation Configuration
 * 
 * Bu konfigürasyon, Content Management Service'i Apollo Federation subgraph
 * olarak yapılandırır.
 * - Content entity resolver
 * - Genre entity resolver
 * - Season entity resolver
 * - Episode entity resolver
 * - CastCrew entity resolver
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    private final ContentService contentService;
    private final GenreService genreService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;
    private final CastCrewService castCrewService;

    public FederationConfig(ContentService contentService,
            GenreService genreService,
            SeasonService seasonService,
            EpisodeService episodeService,
            CastCrewService castCrewService) {
        this.contentService = contentService;
        this.genreService = genreService;
        this.seasonService = seasonService;
        this.episodeService = episodeService;
        this.castCrewService = castCrewService;
    }

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for Content Management Service subgraph");

        // Entity resolver - Gateway'den gelen entity referanslarını çözer
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);

            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");

                        // User entity stub - şimdi doğru Record tipini döndürüyor
                        if ("User".equals(typeName)) {
                            Object userIdObj = representation.get("userId");
                            if (userIdObj != null) {
                                log.debug("Resolving User entity reference for userId: {}", userIdObj);
                                return new ContentGraphQLController.User(userIdObj.toString());
                            }
                            return null;
                        }

                        Object idObj = representation.get("id");

                        if (idObj == null)
                            return null;

                        try {
                            Long id = Long.parseLong(idObj.toString());

                            switch (typeName) {
                                case "Content":
                                    log.debug("Resolving Content entity reference for id: {}", id);
                                    return contentService.getContentById(id);

                                case "Genre":
                                    log.debug("Resolving Genre entity reference for id: {}", id);
                                    return genreService.getGenreById(id);

                                case "Season":
                                    log.debug("Resolving Season entity reference for id: {}", id);
                                    return seasonService.getSeasonById(id);

                                case "Episode":
                                    log.debug("Resolving Episode entity reference for id: {}", id);
                                    return episodeService.getEpisodeById(id);

                                case "CastCrew":
                                    log.debug("Resolving CastCrew entity reference for id: {}", id);
                                    return castCrewService.getCastCrewById(id);

                                default:
                                    return null;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Invalid id format for {}: {}", typeName, idObj);
                            return null;
                        } catch (Exception e) {
                            log.error("Error resolving {} entity: {}", typeName, e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        };

        return builder -> builder.schemaFactory((registry, wiring) -> Federation.transform(registry, wiring)
                .fetchEntities(entityDataFetcher)
                .resolveEntityType(env -> {
                    Object src = env.getObject();
                    if (src instanceof ContentResponse) {
                        return env.getSchema().getObjectType("Content");
                    }
                    if (src instanceof GenreResponse) {
                        return env.getSchema().getObjectType("Genre");
                    }
                    if (src instanceof SeasonResponse) {
                        return env.getSchema().getObjectType("Season");
                    }
                    if (src instanceof EpisodeResponse) {
                        return env.getSchema().getObjectType("Episode");
                    }
                    if (src instanceof CastCrewResponse) {
                        return env.getSchema().getObjectType("CastCrew");
                    }
                    if (src instanceof ContentGraphQLController.User) {
                        return env.getSchema().getObjectType("User");
                    }
                    return null;
                })
                .build());
    }
}
