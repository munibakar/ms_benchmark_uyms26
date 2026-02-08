package com.authentication.microservices.authentication.config;

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
 * Bu konfigürasyon, Authentication Service'i Apollo Federation subgraph olarak yapılandırır.
 * Auth Service mutation odaklıdır (register, login, logout).
 * AuthUser entity'si resolvable: false olarak işaretlenmiştir - User Service tarafından çözülür.
 */
@Configuration
public class FederationConfig {

    private static final Logger log = LoggerFactory.getLogger(FederationConfig.class);

    /**
     * GraphQL Source Builder'ı Federation için özelleştirir
     */
    @Bean
    public GraphQlSourceBuilderCustomizer federationCustomizer() {
        log.info("Configuring Apollo Federation for Authentication Service subgraph");
        
        // Entity resolver - Auth Service AuthUser entity'si için stub resolver
        // resolvable: false olduğu için gerçek çözümleme User Service'te yapılır
        DataFetcher<?> entityDataFetcher = env -> {
            List<Map<String, Object>> representations = env.getArgument(_Entity.argumentName);
            
            return representations.stream()
                    .map(representation -> {
                        String typeName = (String) representation.get("__typename");
                        log.debug("Entity resolution requested for type: {} (Auth Service)", typeName);
                        // AuthUser resolvable: false, User Service çözecek
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
