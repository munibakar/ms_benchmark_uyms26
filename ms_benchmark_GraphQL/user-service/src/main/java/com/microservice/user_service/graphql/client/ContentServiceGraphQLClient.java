package com.microservice.user_service.graphql.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.ContentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

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
     * Tüm içerikleri getir
     * 
     * @return List<ContentInfo> içerik listesi
     */
    public List<ContentInfo> getAllContents() {
        log.debug("Fetching all contents via GraphQL");

        String query = """
                query GetAllContents {
                    getAllContents {
                        id
                        title
                        contentType
                    }
                }
                """;

        try {
            List<ContentInfo> response = graphQlClient.document(query)
                    .retrieve("getAllContents")
                    .toEntityList(ContentInfo.class)
                    .block();

            log.debug("Contents fetched successfully: {}", response);
            return response != null ? response : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching contents, error: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch contents from content-service", e);
        }
    }
}
