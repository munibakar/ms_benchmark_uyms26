package com.microservices.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservices.api_gateway.filter.AuthenticationFilter;

/**
 * Gateway Configuration
 * Route tanımlarını yapar ve authentication filter'ı uygular
 * Hard-coded route yerine Eureka service discovery ile dinamik routing
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
        log.info("Configuring Gateway routes with authentication filter and gateway verification header");
        
        return builder.routes()
                // Authentication Service Routes
                // Docker DNS kullanıyoruz (lb:// yerine http://)
                .route("authentication-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://authentication-service:8000"))
                
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://user-service:9000"))
                
                // Subscription and Billing Service Routes
                .route("subscription-and-billing-service", r -> r
                        .path("/api/subscription/**", "/api/billing/**", "/api/payment/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://subscription-and-billing-service:9100"))
                
                // Profile Service Routes
                .route("profile-service", r -> r
                        .path("/api/profiles/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://profile-service:9001"))
                
                // Content Management Service Routes
                .route("content-management-service", r -> r
                        .path("/api/contents/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://content-management-service:9200"))
                
                // Video Streaming Service Routes
                .route("video-streaming-service", r -> r
                        .path("/api/stream/**")
                        .filters(f -> f
                                // .stripPrefix(1)  // Prefix kaldırma yok, direkt yönlendirme
                                .addRequestHeader("X-Gateway-Request", "true")  // Gateway verification header
                                .filter(authenticationFilter))
                        .uri("http://video-streaming-service:9300"))
                
                // Diğer servisler buraya eklenebilir
                // Örnek: Movie Service, Content Service vb.
                // Her serviste mutlaka .addRequestHeader("X-Gateway-Request", "true") eklemeyi unutmayın!
                
                .build();
    }
}
