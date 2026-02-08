package com.microservices.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global Logging Filter
 * API Gateway'den geçen TÜM istekleri ve yanıtları loglar
 * Bu filter tüm route'lara otomatik olarak uygulanır
 */
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Request bilgilerini topla
        String requestId = getRequestId(exchange);
        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();
        String queryParams = request.getURI().getQuery();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String clientIp = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "UNKNOWN";
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        
        // Request başlangıç zamanı
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(formatter);
        
        // Request detaylarını logla
        logRequest(requestId, timestamp, method, path, queryParams, clientIp, userAgent);
        
        // Request headers'ı logla (sensitive bilgiler hariç)
        logRequestHeaders(requestId, request.getHeaders());
        
        // Response'u intercept et ve logla
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Response detaylarını logla
            logResponse(requestId, response.getStatusCode(), duration);
            
            // Performance uyarısı
            if (duration > 3000) {
                log.warn("🐌 SLOW REQUEST [{}] - Duration: {}ms - Path: {}", 
                    requestId, duration, path);
            }
        }));
    }

    /**
     * Request detaylarını logla
     */
    private void logRequest(String requestId, String timestamp, HttpMethod method, 
                           String path, String queryParams, String clientIp, String userAgent) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("\n========================================\n");
        logMsg.append("📨 INCOMING REQUEST\n");
        logMsg.append("========================================\n");
        logMsg.append(String.format("🆔 Request ID: %s\n", requestId));
        logMsg.append(String.format("⏰ Timestamp: %s\n", timestamp));
        logMsg.append(String.format("🔹 Method: %s\n", method));
        logMsg.append(String.format("🔹 Path: %s\n", path));
        
        if (queryParams != null && !queryParams.isEmpty()) {
            logMsg.append(String.format("🔹 Query Params: %s\n", queryParams));
        }
        
        logMsg.append(String.format("🌐 Client IP: %s\n", clientIp));
        
        if (userAgent != null) {
            logMsg.append(String.format("🖥️  User Agent: %s\n", userAgent));
        }
        
        logMsg.append("========================================");
        
        log.info(logMsg.toString());
    }

    /**
     * Request headers'ı logla (sensitive bilgiler maskelenmiş)
     */
    private void logRequestHeaders(String requestId, HttpHeaders headers) {
        if (log.isDebugEnabled()) {
            StringBuilder headerLog = new StringBuilder();
            headerLog.append(String.format("\n📋 REQUEST HEADERS [%s]:\n", requestId));
            
            headers.forEach((name, values) -> {
                // Sensitive header'ları maskele
                if (name.equalsIgnoreCase("Authorization") || 
                    name.equalsIgnoreCase("Cookie") ||
                    name.equalsIgnoreCase("X-API-Key")) {
                    headerLog.append(String.format("  %s: ***MASKED***\n", name));
                } else {
                    headerLog.append(String.format("  %s: %s\n", name, String.join(", ", values)));
                }
            });
            
            log.debug(headerLog.toString());
        }
    }

    /**
     * Response detaylarını logla
     */
    private void logResponse(String requestId, org.springframework.http.HttpStatusCode statusCode, long duration) {
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("\n========================================\n");
        logMsg.append("📤 OUTGOING RESPONSE\n");
        logMsg.append("========================================\n");
        logMsg.append(String.format("🆔 Request ID: %s\n", requestId));
        
        if (statusCode != null) {
            String statusEmoji = getStatusEmoji(statusCode.value());
            logMsg.append(String.format("📊 Status: %s %s\n", statusCode.value(), statusEmoji));
        }
        
        logMsg.append(String.format("⏱️  Duration: %d ms\n", duration));
        logMsg.append("========================================");
        
        // Status code'a göre log level belirle
        if (statusCode != null) {
            int status = statusCode.value();
            if (status >= 500) {
                log.error(logMsg.toString());
            } else if (status >= 400) {
                log.warn(logMsg.toString());
            } else {
                log.info(logMsg.toString());
            }
        } else {
            log.info(logMsg.toString());
        }
    }

    /**
     * Status code için emoji döndür
     */
    private String getStatusEmoji(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "✅ SUCCESS";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "↪️ REDIRECT";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "⚠️ CLIENT ERROR";
        } else if (statusCode >= 500) {
            return "❌ SERVER ERROR";
        }
        return "ℹ️";
    }

    /**
     * Request ID'yi al veya oluştur
     */
    private String getRequestId(ServerWebExchange exchange) {
        // Eğer önceki filter tarafından eklenmiş ise onu kullan
        String existingRequestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
        return existingRequestId != null ? existingRequestId : 
               exchange.getRequest().getId();
    }

    /**
     * Bu filter'ın önceliği - en yüksek öncelik (ilk çalışan)
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

