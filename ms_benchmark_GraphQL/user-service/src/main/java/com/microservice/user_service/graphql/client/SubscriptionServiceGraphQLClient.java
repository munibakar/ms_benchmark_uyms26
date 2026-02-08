package com.microservice.user_service.graphql.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.PaymentInfo;
import com.microservice.user_service.dto.response.UserDashboardResponse.SubscriptionInfo;
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
     * @return SubscriptionInfo aktif abonelik bilgisi
     */
    public SubscriptionInfo getActiveSubscription(String userId) {
        log.debug("Fetching active subscription for userId: {} via GraphQL", userId);

        String query = """
                query GetActiveSubscription($userId: String!) {
                    getActiveSubscription(userId: $userId) {
                        id
                        planName
                        status
                        billingCycle
                    }
                }
                """;

        try {
            SubscriptionInfo response = graphQlClient.document(query)
                    .variable("userId", userId)
                    .retrieve("getActiveSubscription")
                    .toEntity(SubscriptionInfo.class)
                    .block();

            log.debug("Subscription fetched successfully: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error fetching subscription for userId: {}, error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch subscription from subscription-service", e);
        }
    }

    /**
     * Kullanıcının fatura geçmişini getir
     * 
     * @param userId Kullanıcı ID
     * @return List<PaymentInfo> ödeme listesi
     */
    public List<PaymentInfo> getBillingHistory(String userId) {
        log.debug("Fetching billing history for userId: {} via GraphQL", userId);

        String query = """
                query GetBillingHistory($userId: String!) {
                    getBillingHistory(userId: $userId) {
                        id
                        amount
                        status
                        paymentDate
                    }
                }
                """;

        try {
            List<PaymentInfo> response = graphQlClient.document(query)
                    .variable("userId", userId)
                    .retrieve("getBillingHistory")
                    .toEntityList(PaymentInfo.class)
                    .block();

            log.debug("Billing history fetched successfully: {}", response);
            return response != null ? response : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching billing history for userId: {}, error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch billing history from subscription-service", e);
        }
    }
}
