package com.microservices.video_streaming_service.client;

import com.microservices.video_streaming_service.dto.response.ContentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

/**
 * Content Service GraphQL Client
 * Content Management Service'e GraphQL istekleri göndermek için kullanılır
 */
@Component
public class ContentServiceGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(ContentServiceGraphQLClient.class);

    @Value("${application.config.content-service-url:http://content-management-service:9200}")
    private String contentServiceUrl;

    private HttpGraphQlClient graphQlClient;

    @PostConstruct
    public void init() {
        WebClient webClient = WebClient.builder()
                .baseUrl(contentServiceUrl + "/graphql")
                .build();
        this.graphQlClient = HttpGraphQlClient.builder(webClient).build();
        log.info("ContentServiceGraphQLClient initialized with URL: {}/graphql", contentServiceUrl);
    }

    /**
     * Content ID'ye göre içerik bilgilerini getir
     * 
     * @param contentId Content ID
     * @return ContentResponse
     */
    public ContentResponse getContentById(Long contentId) {
        log.debug("Fetching content by id: {} via GraphQL", contentId);

        String query = """
                query GetContentById($contentId: ID!) {
                    getContentById(contentId: $contentId) {
                        id
                        title
                        videoFilePath
                        contentType
                        isActive
                    }
                }
                """;

        try {
            ContentResponse response = graphQlClient.document(query)
                    .variable("contentId", contentId.toString())
                    .retrieve("getContentById")
                    .toEntity(ContentResponse.class)
                    .block();

            log.debug("Content fetched successfully: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching content by id: {}, error: {}", contentId, e.getMessage());
            throw new RuntimeException("Failed to fetch content from content-service", e);
        }
    }

    /**
     * Episode ID'ye göre episode bilgilerini getir
     * 
     * @param episodeId Episode ID
     * @return EpisodeResponse
     */
    public EpisodeResponse getEpisodeById(Long episodeId) {
        log.debug("Fetching episode by id: {} via GraphQL", episodeId);

        String query = """
                query GetEpisodeById($episodeId: ID!) {
                    getEpisodeById(episodeId: $episodeId) {
                        id
                        title
                        videoFilePath
                        isActive
                    }
                }
                """;

        try {
            EpisodeResponse response = graphQlClient.document(query)
                    .variable("episodeId", episodeId.toString())
                    .retrieve("getEpisodeById")
                    .toEntity(EpisodeResponse.class)
                    .block();

            log.debug("Episode fetched successfully: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching episode by id: {}, error: {}", episodeId, e.getMessage());
            throw new RuntimeException("Failed to fetch episode from content-service", e);
        }
    }

    /**
     * Episode Response DTO
     * Episode bilgisi için iç sınıf
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeResponse {
        private Long id;
        private String title;
        private String videoFilePath;
        private Boolean isActive;
    }
}
