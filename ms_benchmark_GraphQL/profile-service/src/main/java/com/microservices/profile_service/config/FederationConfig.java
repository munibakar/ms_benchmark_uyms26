package com.microservices.profile_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.microservices.profile_service.controller.ProfileGraphQLController;
import com.microservices.profile_service.dto.response.ProfileResponse;
import com.microservices.profile_service.repository.ProfileRepository;
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
 * Bu konfigürasyon, Profile Service'i Apollo Federation subgraph olarak
 * yapılandırır.
 * - Profile entity resolver
 * - User entity için profiles field resolver (extend type User)
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    private final ProfileRepository profileRepository;

    public FederationConfig(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for Profile Service subgraph");

        // Entity resolver - Gateway'den gelen entity referanslarını çözer
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);

            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");

                        if ("Profile".equals(typeName)) {
                            // Profile entity'si için referans çözümlemesi
                            Object idObj = representation.get("id");
                            if (idObj != null) {
                                log.debug("Resolving Profile entity reference for id: {}", idObj);
                                try {
                                    Long id = Long.parseLong(idObj.toString());
                                    return profileRepository.findByIdAndNotDeleted(id)
                                            .map(ProfileResponse::fromEntity)
                                            .orElse(null);
                                } catch (NumberFormatException e) {
                                    log.error("Invalid Profile id format: {}", idObj);
                                    return null;
                                }
                            }
                        } else if ("User".equals(typeName)) {
                            // User entity stub - şimdi doğru Record tipini döndürüyor
                            Object userIdObj = representation.get("userId");
                            if (userIdObj != null) {
                                log.debug("Resolving User entity reference for userId: {}", userIdObj);
                                return new ProfileGraphQLController.User(userIdObj.toString());
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
                    if (src instanceof ProfileResponse) {
                        return env.getSchema().getObjectType("Profile");
                    }
                    if (src instanceof ProfileGraphQLController.User) {
                        return env.getSchema().getObjectType("User");
                    }
                    return null;
                })
                .build());
    }
}
