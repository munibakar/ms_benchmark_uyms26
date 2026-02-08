package com.microservices.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservices.api_gateway.filter.AuthenticationFilter;

/**
 * Gateway Configuration - Pure GraphQL Architecture
 * 
 * REST API route'ları kaldırıldı - Tüm işlemler GraphQL Gateway (port 4000)
 * üzerinden.
 * Bu gateway sadece video streaming (binary data) için kullanılır.
 * 
 * GraphQL Endpoint: http://graphql-gateway:4000/graphql
 * Video Streaming: http://api-gateway:8765/api/stream/**
 */
@Configuration
public class GatewayConfig {

        private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

        private final AuthenticationFilter authenticationFilter;

        public GatewayConfig(AuthenticationFilter authenticationFilter) {
                this.authenticationFilter = authenticationFilter;
        }

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                log.info("Configuring Gateway routes - Pure GraphQL Mode (only video streaming via REST)");

                return builder.routes()
                                // Video Streaming Service Routes
                                // Binary video data GraphQL ile taşınamaz, REST API olarak kalıyor
                                .route("video-streaming-service", r -> r
                                                .path("/api/stream/**")
                                                .filters(f -> f
                                                                .addRequestHeader("X-Gateway-Request", "true")
                                                                .filter(authenticationFilter))
                                                .uri("http://video-streaming-service:9300"))

                                // GraphQL Gateway proxy (opsiyonel - nginx'ten direkt yönlendirilebilir)
                                .route("graphql-gateway", r -> r
                                                .path("/graphql", "/graphql/**")
                                                .filters(f -> f
                                                                .addRequestHeader("X-Gateway-Request", "true"))
                                                .uri("http://graphql-gateway:4000"))

                                .build();
        }
}
