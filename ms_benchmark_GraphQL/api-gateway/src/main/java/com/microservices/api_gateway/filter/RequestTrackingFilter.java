package com.microservices.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Request Tracking Filter
 * Her isteğe benzersiz bir Request ID ekler
 * Bu ID ile request'in tüm microservice'ler arasındaki yolculuğu takip edilebilir
 * 
 * Distributed tracing için temel altyapı sağlar
 */
@Component
public class RequestTrackingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestTrackingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String GATEWAY_START_TIME = "gateway-start-time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Request ID - client tarafından gönderilmişse onu kullan, yoksa yeni oluştur
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = generateRequestId();
            log.debug("Generated new Request ID: {}", requestId);
        } else {
            log.debug("Using existing Request ID: {}", requestId);
        }
        
        // Correlation ID - microservice'ler arası ilişkilendirme için
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = requestId; // İlk istek için correlation ID = request ID
        }
        
        // Final değişkenler (lambda içinde kullanılacak)
        final String finalRequestId = requestId;
        final String finalCorrelationId = correlationId;
        
        // Start time'ı exchange attribute'una kaydet (performance monitoring için)
        exchange.getAttributes().put(GATEWAY_START_TIME, System.currentTimeMillis());
        
        // Request'e tracking header'ları ekle
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, finalRequestId)
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .header("X-Gateway-Name", "api-gateway")
                .header("X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()))
                .build();
        
        log.info("🔍 Request Tracking: ID={}, Correlation={}, Path={}", 
                finalRequestId, finalCorrelationId, request.getURI().getPath());
        
        // Modified request ile devam et
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    // Request tamamlandığında
                    Long startTime = exchange.getAttribute(GATEWAY_START_TIME);
                    if (startTime != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("✅ Request Completed: ID={}, Duration={}ms, Signal={}", 
                                finalRequestId, duration, signalType);
                    }
                });
    }

    /**
     * Benzersiz Request ID oluştur
     * Format: GW-{timestamp}-{short-uuid}
     */
    private String generateRequestId() {
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("GW-%d-%s", System.currentTimeMillis(), shortUuid);
    }

    /**
     * Bu filter GlobalLoggingFilter'dan önce çalışmalı
     * Order: HIGHEST_PRECEDENCE + 1
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

