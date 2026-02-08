package com.microservices.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance Monitoring Filter
 * API Gateway'den geçen isteklerin performans metriklerini toplar
 * - İstek süreleri
 * - Toplam istek sayısı
 * - Başarılı/Başarısız istek sayısı
 * - Endpoint bazlı istatistikler
 */
@Component
public class PerformanceMonitoringFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringFilter.class);
    private static final String METRICS_START_TIME = "metrics-start-time";
    
    // Metrikler için in-memory storage (production'da Redis veya Prometheus kullanılabilir)
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);
    private final AtomicLong totalDuration = new AtomicLong(0);
    
    // Endpoint bazlı metrikler
    private final ConcurrentHashMap<String, EndpointMetrics> endpointMetrics = new ConcurrentHashMap<>();
    
    // Performans eşikleri (ms)
    private static final long SLOW_REQUEST_THRESHOLD = 1000;
    private static final long VERY_SLOW_REQUEST_THRESHOLD = 3000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String endpoint = method + " " + path;
        
        // Başlangıç zamanını kaydet
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(METRICS_START_TIME, startTime);
        
        // Toplam istek sayısını artır
        int requestCount = totalRequests.incrementAndGet();
        
        // Her 100 istekte bir özet logla
        if (requestCount % 100 == 0) {
            logMetricsSummary();
        }
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // Request başarılı
                    long duration = calculateDuration(exchange);
                    ServerHttpResponse response = exchange.getResponse();
                    int statusCode = response.getStatusCode() != null ? 
                            response.getStatusCode().value() : 0;
                    
                    recordMetrics(endpoint, duration, statusCode, true);
                    logPerformance(endpoint, duration, statusCode);
                })
                .doOnError(error -> {
                    // Request başarısız
                    long duration = calculateDuration(exchange);
                    recordMetrics(endpoint, duration, 500, false);
                    log.error("❌ Request Failed: {} - Duration: {}ms - Error: {}", 
                            endpoint, duration, error.getMessage());
                });
    }

    /**
     * İstek süresini hesapla
     */
    private long calculateDuration(ServerWebExchange exchange) {
        Long startTime = exchange.getAttribute(METRICS_START_TIME);
        if (startTime != null) {
            return System.currentTimeMillis() - startTime;
        }
        return -1;
    }

    /**
     * Metrikleri kaydet
     */
    private void recordMetrics(String endpoint, long duration, int statusCode, boolean isSuccess) {
        // Global metrikler
        totalDuration.addAndGet(duration);
        
        if (isSuccess && statusCode >= 200 && statusCode < 400) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        
        // Endpoint bazlı metrikler
        endpointMetrics.computeIfAbsent(endpoint, k -> new EndpointMetrics())
                .record(duration, isSuccess);
    }

    /**
     * Performans logla
     */
    private void logPerformance(String endpoint, long duration, int statusCode) {
        if (duration >= VERY_SLOW_REQUEST_THRESHOLD) {
            log.warn("🐌 VERY SLOW REQUEST: {} - Duration: {}ms - Status: {}", 
                    endpoint, duration, statusCode);
        } else if (duration >= SLOW_REQUEST_THRESHOLD) {
            log.warn("⚠️ SLOW REQUEST: {} - Duration: {}ms - Status: {}", 
                    endpoint, duration, statusCode);
        } else if (log.isDebugEnabled()) {
            log.debug("⚡ FAST REQUEST: {} - Duration: {}ms - Status: {}", 
                    endpoint, duration, statusCode);
        }
    }

    /**
     * Metriklerin özetini logla
     */
    private void logMetricsSummary() {
        int total = totalRequests.get();
        int successful = successfulRequests.get();
        int failed = failedRequests.get();
        long avgDuration = total > 0 ? totalDuration.get() / total : 0;
        
        StringBuilder summary = new StringBuilder();
        summary.append("\n╔══════════════════════════════════════════════════════════════╗\n");
        summary.append("║          📊 API GATEWAY PERFORMANCE METRICS               ║\n");
        summary.append("╠══════════════════════════════════════════════════════════════╣\n");
        summary.append(String.format("║ 📈 Total Requests:       %-32d║\n", total));
        summary.append(String.format("║ ✅ Successful Requests:  %-32d║\n", successful));
        summary.append(String.format("║ ❌ Failed Requests:      %-32d║\n", failed));
        summary.append(String.format("║ ⚡ Average Duration:     %-28s ms║\n", avgDuration));
        summary.append(String.format("║ 📊 Success Rate:         %-28s %%║\n", 
                total > 0 ? String.format("%.2f", (successful * 100.0 / total)) : "N/A"));
        summary.append("╠══════════════════════════════════════════════════════════════╣\n");
        summary.append("║                   TOP 5 ENDPOINTS                         ║\n");
        summary.append("╠══════════════════════════════════════════════════════════════╣\n");
        
        // Top 5 endpoint'leri göster (en çok kullanılan)
        endpointMetrics.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getCount(), e1.getValue().getCount()))
                .limit(5)
                .forEach(entry -> {
                    String endpoint = entry.getKey();
                    EndpointMetrics metrics = entry.getValue();
                    
                    // Endpoint'i kısalt (çok uzunsa)
                    if (endpoint.length() > 40) {
                        endpoint = endpoint.substring(0, 37) + "...";
                    }
                    
                    summary.append(String.format("║ 🔹 %-43s║\n", endpoint));
                    summary.append(String.format("║    Count: %-8d Avg: %-8d ms Success: %5.1f%% ║\n", 
                            metrics.getCount(), 
                            metrics.getAverageDuration(),
                            metrics.getSuccessRate()));
                });
        
        summary.append("╚══════════════════════════════════════════════════════════════╝");
        
        log.info(summary.toString());
    }

    @Override
    public int getOrder() {
        // RequestTrackingFilter'dan sonra çalışmalı
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    /**
     * Endpoint bazlı metrik sınıfı
     */
    private static class EndpointMetrics {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong minDuration = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxDuration = new AtomicLong(0);

        public void record(long duration, boolean success) {
            count.incrementAndGet();
            if (success) {
                successCount.incrementAndGet();
            }
            totalDuration.addAndGet(duration);
            
            // Min duration güncelle
            updateMin(duration);
            
            // Max duration güncelle
            updateMax(duration);
        }

        private void updateMin(long duration) {
            long currentMin;
            do {
                currentMin = minDuration.get();
                if (duration >= currentMin) {
                    break;
                }
            } while (!minDuration.compareAndSet(currentMin, duration));
        }

        private void updateMax(long duration) {
            long currentMax;
            do {
                currentMax = maxDuration.get();
                if (duration <= currentMax) {
                    break;
                }
            } while (!maxDuration.compareAndSet(currentMax, duration));
        }

        public int getCount() {
            return count.get();
        }

        public long getAverageDuration() {
            int c = count.get();
            return c > 0 ? totalDuration.get() / c : 0;
        }

        public double getSuccessRate() {
            int c = count.get();
            return c > 0 ? (successCount.get() * 100.0 / c) : 0.0;
        }
    }
}

