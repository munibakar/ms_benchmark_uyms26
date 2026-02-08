package com.microservice.user_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.microservice.user_service.dto.response.UserProfileResponse;
import com.microservice.user_service.service.UserProfileService;
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
 * Bu konfigürasyon, User Service'i Apollo Federation subgraph olarak yapılandırır.
 * Gateway, bu servisin schema'sını diğer subgraph'larla birleştirir.
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    private final UserProfileService userProfileService;

    public FederationConfig(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for User Service subgraph");
        
        // Entity resolver - Gateway'den gelen entity referanslarını çözer
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);
            
            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");
                        
                        if ("User".equals(typeName)) {
                            // User entity'si için referans çözümlemesi
                            Object idObj = representation.get("id");
                            if (idObj != null) {
                                String odValue = idObj.toString();
                                log.debug("Resolving User entity reference for id: {}", idObj);
                                try {
                                    // ID long olarak geliyorsa direkt kullan, değilse userId olarak ara
                                    Long id = Long.parseLong(odValue);
                                    return userProfileService.getUserProfileById(id);
                                } catch (NumberFormatException e) {
                                    // userId string olarak geldi
                                    return userProfileService.getUserProfileByUserId(odValue);
                                }
                            }
                        }
                        
                        return null;
                    })
                    .collect(Collectors.toList());
        };

        return builder -> builder.schemaFactory((registry, wiring) -> 
            Federation.transform(registry, wiring)
                    .fetchEntities(entityDataFetcher)
                    .resolveEntityType(env -> {
                        Object src = env.getObject();
                        if (src instanceof UserProfileResponse) {
                            return env.getSchema().getObjectType("User");
                        }
                        return null;
                    })
                    .build()
        );
    }
}
