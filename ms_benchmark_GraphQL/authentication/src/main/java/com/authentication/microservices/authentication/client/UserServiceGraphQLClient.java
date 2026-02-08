package com.authentication.microservices.authentication.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

/**
 * User Service GraphQL Client
 * User Service'e GraphQL istekleri göndermek için kullanılır (REST yerine)
 */
@Component
public class UserServiceGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceGraphQLClient.class);

    @Value("${application.config.user-service-url:http://user-service:9000}")
    private String userServiceUrl;

    private HttpGraphQlClient graphQlClient;

    @PostConstruct
    public void init() {
        WebClient webClient = WebClient.builder()
                .baseUrl(userServiceUrl + "/graphql")
                .build();
        this.graphQlClient = HttpGraphQlClient.builder(webClient).build();
        log.info("UserServiceGraphQLClient initialized with URL: {}/graphql", userServiceUrl);
    }

    /**
     * User Service'te user profile oluştur
     * mutation createUserProfile(input: CreateUserProfileInput!)
     */
    public void createUserProfile(String userId, String email, String firstName, String lastName) {
        log.info("Sending createUserProfile mutation for userId: {}", userId);

        String mutation = """
                mutation CreateUserProfile($input: CreateUserProfileInput!) {
                    createUserProfile(input: $input) {
                        id
                        userId
                        email
                    }
                }
                """;

        try {
            graphQlClient.document(mutation)
                    .variable("input", new CreateUserProfileInput(userId, email, firstName, lastName))
                    .retrieve("createUserProfile")
                    .toEntity(Object.class) // Response tipi kritik değil, sadece hata almamak yeterli
                    .block();

            log.info("Successfully created user profile in User Service for userId: {}", userId);
        } catch (Exception e) {
            log.error("Failed to create user profile in User Service: {}", e.getMessage());
            // Retry logic or DLQ could be added here
            throw new RuntimeException("Failed to propagate user creation to User Service", e);
        }
    }

    private record CreateUserProfileInput(
            String userId,
            String email,
            String firstName,
            String lastName) {
    }
}
