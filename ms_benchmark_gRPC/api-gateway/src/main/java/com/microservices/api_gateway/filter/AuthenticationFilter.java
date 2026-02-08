package com.microservices.api_gateway.filter;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.microservices.api_gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

/**
 * Authentication Filter
 * JWT token validation yapar
 * Public endpointler hariç tüm isteklerde token kontrolü
 */
@Component
public class AuthenticationFilter implements GatewayFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    private final JwtUtil jwtUtil;
    private final List<String> publicPaths;

    public AuthenticationFilter(JwtUtil jwtUtil, 
                               @Value("${gateway.public-paths:}") String publicPathsConfig) {
        this.jwtUtil = jwtUtil;
        // Properties'den virgülle ayrılmış public path'leri oku ve trim et
        this.publicPaths = Arrays.stream(publicPathsConfig.split(","))
                .map(String::trim)
                .filter(path -> !path.isEmpty())
                .toList();
        
        log.info("🔓 Configured public paths: {}", this.publicPaths);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // Public endpointler (authentication gerektirmeyen)
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint, skipping authentication: {}", path);
            return chain.filter(exchange);
        }
        
        // Authorization header kontrolü
        if (!request.getHeaders().containsKey("Authorization")) {
            log.warn("Missing Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header format for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        // Token validation
        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Token geçerli, user bilgilerini header'a ekle
        String email = jwtUtil.getEmailFromToken(token);
        String userId = jwtUtil.getUserIdFromToken(token);
        
        if (email != null && userId != null) {
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Id", userId)
                    .build();
            
            log.debug("Authenticated request for user: {} (ID: {})", email, userId);
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        
        log.warn("Failed to extract user info from token");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    /**
     * Public endpoint kontrolü
     * Hem properties'den okunan path'leri hem de sistem endpoint'lerini kontrol eder
     */
    private boolean isPublicEndpoint(String path) {
        // Actuator ve Eureka endpoint'leri her zaman public
        if (path.startsWith("/actuator/") || path.startsWith("/eureka/")) {
            return true;
        }
        
        // Properties'den okunan public path'leri kontrol et
        // Hem direkt path hem de service prefix'li path'i kontrol et
        for (String publicPath : publicPaths) {
            // Direkt eşleşme (örn: /api/subscription/health)
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                log.debug("✅ Public endpoint matched: {} -> {}", path, publicPath);
                return true;
            }
            
            // Service prefix'li eşleşme kontrolü
            // Örn: /subscription/api/subscription/health için /api/subscription/health kontrolü
            // Path'i service ismiyle başlıyorsa, service ismini çıkar ve tekrar kontrol et
            if (path.contains("/")) {
                String[] parts = path.split("/", 3); // ["", "subscription", "api/subscription/health"]
                if (parts.length >= 3) {
                    String pathWithoutService = "/" + parts[2]; // "/api/subscription/health"
                    if (pathWithoutService.equals(publicPath) || pathWithoutService.startsWith(publicPath + "/")) {
                        log.debug("✅ Public endpoint matched (with service prefix): {} -> {}", path, publicPath);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}

