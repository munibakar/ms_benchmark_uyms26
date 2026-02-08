package com.microservice.user_service.graphql.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.ProfileInfo;
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
 * Profile Service GraphQL Client
 * Profile Service'e GraphQL istekleri göndermek için kullanılır
 */
@Component
public class ProfileServiceGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceGraphQLClient.class);

    @Value("${application.config.profile-service-url:http://profile-service:9002}")
    private String profileServiceUrl;

    private HttpGraphQlClient graphQlClient;

    @PostConstruct
    public void init() {
        WebClient webClient = WebClient.builder()
                .baseUrl(profileServiceUrl + "/graphql")
                .build();
        this.graphQlClient = HttpGraphQlClient.builder(webClient).build();
        log.info("ProfileServiceGraphQLClient initialized with URL: {}/graphql", profileServiceUrl);
    }

    /**
     * Hesap ID'ye göre profilleri getir
     * 
     * @param accountId Hesap ID (userId)
     * @return List<ProfileInfo> profil listesi
     */
    public List<ProfileInfo> getProfilesByAccountId(String accountId) {
        log.debug("Fetching profiles for accountId: {} via GraphQL", accountId);

        String query = """
                query GetProfilesByAccountId($accountId: String!) {
                    getProfilesByAccountId(accountId: $accountId) {
                        id
                        profileName
                        avatarUrl
                        isChildProfile
                    }
                }
                """;

        try {
            List<ProfileInfo> response = graphQlClient.document(query)
                    .variable("accountId", accountId)
                    .retrieve("getProfilesByAccountId")
                    .toEntityList(ProfileInfo.class)
                    .block();

            log.debug("Profiles fetched successfully: {}", response);
            return response != null ? response : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching profiles for accountId: {}, error: {}", accountId, e.getMessage());
            throw new RuntimeException("Failed to fetch profiles from profile-service", e);
        }
    }
}
