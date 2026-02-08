package com.microservices.video_streaming_service.client;

import com.microservices.video_streaming_service.dto.response.SubscriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

/**
 * Subscription Service GraphQL Client
 * Subscription and Billing Service'e GraphQL istekleri göndermek için
 * kullanılır
 */
@Component
public class SubscriptionServiceGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceGraphQLClient.class);

    @Value("${application.config.subscription-service-url:http://subscription-and-billing-service:9100}")
    private String subscriptionServiceUrl;

    private HttpGraphQlClient graphQlClient;

    @PostConstruct
    public void init() {
        WebClient webClient = WebClient.builder()
                .baseUrl(subscriptionServiceUrl + "/graphql")
                .build();
        this.graphQlClient = HttpGraphQlClient.builder(webClient).build();
        log.info("SubscriptionServiceGraphQLClient initialized with URL: {}/graphql", subscriptionServiceUrl);
    }

    /**
     * Kullanıcının aktif aboneliğini getir
     * 
     * @param userId Kullanıcı ID
     * @return SubscriptionResponse aktif abonelik bilgisi
     */
    public SubscriptionResponse getActiveSubscription(String userId) {
        log.debug("Fetching active subscription for userId: {} via GraphQL", userId);

        String query = """
                query GetActiveSubscription($userId: String!) {
                    getActiveSubscription(userId: $userId) {
                        id
                        userId
                        status
                        billingCycle
                        startDate
                        endDate
                        autoRenew
                    }
                }
                """;

        try {
            SubscriptionResponse response = graphQlClient.document(query)
                    .variable("userId", userId)
                    .retrieve("getActiveSubscription")
                    .toEntity(SubscriptionResponse.class)
                    .block();

            log.debug("Subscription fetched successfully: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching subscription for userId: {}, error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch subscription from subscription-service", e);
        }
    }
}
