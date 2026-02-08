package com.microservices.subscription_and_billing_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.microservices.subscription_and_billing_service.controller.SubscriptionGraphQLController;
import com.microservices.subscription_and_billing_service.dto.response.BillingHistoryResponse;
import com.microservices.subscription_and_billing_service.dto.response.SubscriptionResponse;
import com.microservices.subscription_and_billing_service.repository.BillingHistoryRepository;
import com.microservices.subscription_and_billing_service.repository.SubscriptionRepository;
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
 * Bu konfigürasyon, Subscription Service'i Apollo Federation subgraph olarak
 * yapılandırır.
 * - Subscription entity resolver
 * - BillingHistory entity resolver
 * - User entity için subscription ve billingHistory field resolver
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    private final SubscriptionRepository subscriptionRepository;
    private final BillingHistoryRepository billingHistoryRepository;

    public FederationConfig(SubscriptionRepository subscriptionRepository,
            BillingHistoryRepository billingHistoryRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingHistoryRepository = billingHistoryRepository;
    }

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for Subscription Service subgraph");

        // Entity resolver - Gateway'den gelen entity referanslarını çözer
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);

            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");

                        if ("Subscription".equals(typeName)) {
                            Object idObj = representation.get("id");
                            if (idObj != null) {
                                log.debug("Resolving Subscription entity reference for id: {}", idObj);
                                try {
                                    Long id = Long.parseLong(idObj.toString());
                                    return subscriptionRepository.findById(id)
                                            .map(SubscriptionResponse::fromEntity)
                                            .orElse(null);
                                } catch (NumberFormatException e) {
                                    log.error("Invalid Subscription id format: {}", idObj);
                                    return null;
                                }
                            }
                        } else if ("BillingHistory".equals(typeName)) {
                            Object idObj = representation.get("id");
                            if (idObj != null) {
                                log.debug("Resolving BillingHistory entity reference for id: {}", idObj);
                                try {
                                    Long id = Long.parseLong(idObj.toString());
                                    return billingHistoryRepository.findById(id)
                                            .map(BillingHistoryResponse::fromEntity)
                                            .orElse(null);
                                } catch (NumberFormatException e) {
                                    log.error("Invalid BillingHistory id format: {}", idObj);
                                    return null;
                                }
                            }
                        } else if ("User".equals(typeName)) {
                            // User entity stub - şimdi doğru Record tipini döndürüyor
                            Object userIdObj = representation.get("userId");
                            if (userIdObj != null) {
                                log.debug("Resolving User entity reference for userId: {}", userIdObj);
                                return new SubscriptionGraphQLController.User(userIdObj.toString());
                            }
                        }

                        return null;
                    })
                    .collect(Collectors.toList());
        };

        return builder -> builder.schemaFactory((registry, wiring) -> Federation.transform(registry, wiring)
                .fetchEntities(entityDataFetcher)
                .resolveEntityType(env -> {
                    Object src = env.getObject();
                    if (src instanceof SubscriptionResponse) {
                        return env.getSchema().getObjectType("Subscription");
                    }
                    if (src instanceof BillingHistoryResponse) {
                        return env.getSchema().getObjectType("BillingHistory");
                    }
                    if (src instanceof SubscriptionGraphQLController.User) {
                        return env.getSchema().getObjectType("User");
                    }
                    return null;
                })
                .build());
    }
}
