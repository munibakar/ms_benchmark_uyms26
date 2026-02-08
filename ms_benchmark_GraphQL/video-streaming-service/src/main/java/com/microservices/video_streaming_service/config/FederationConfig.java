package com.microservices.video_streaming_service.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
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
 * Bu konfigürasyon, Video Streaming Service'i Apollo Federation subgraph olarak yapılandırır.
 * Video streaming servisi metadata sorguları sağlar, gerçek video streaming REST üzerinden yapılır.
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for Video Streaming Service subgraph");
        
        // Entity resolver - Video streaming için entity resolution minimal
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);
            
            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");
                        log.debug("Entity resolution requested for type: {}", typeName);
                        // Video streaming servisi entity resolution yapmaz
                        // Metadata doğrudan Content Service'ten gelir
                        return null;
                    })
                    .collect(Collectors.toList());
        };

        return builder -> builder.schemaFactory((registry, wiring) -> 
            Federation.transform(registry, wiring)
                    .fetchEntities(entityDataFetcher)
                    .resolveEntityType(env -> null)
                    .build()
        );
    }
}
