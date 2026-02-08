package com.authentication.microservices.authentication.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gateway Verification Filter
 * 
 * Bu filter, tüm isteklerin API Gateway üzerinden geldiğini doğrular.
 * Direkt servis erişimini engeller ve güvenlik sağlar.
 * 
 * API Gateway her istekte "X-Gateway-Request: true" header'ını ekler.
 * Bu header yoksa istek reddedilir.
 * 
 * Güvenlik Katmanları:
 * 1. Docker Network Isolation (ports expose edilmemiş)
 * 2. Gateway Verification Header (bu filter)
 * 
 * @author Netflix Clone Microservices Team
 */
@Component
@Order(1) // En önce çalışacak filter
public class GatewayVerificationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GatewayVerificationFilter.class);
    private static final String GATEWAY_HEADER = "X-Gateway-Request";
    private static final String GATEWAY_HEADER_VALUE = "true";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String gatewayHeader = httpRequest.getHeader(GATEWAY_HEADER);
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Actuator health endpoint'leri ve Custom Health check için bypass
        if (requestURI.startsWith("/actuator/health") || requestURI.equals("/api/auth/health")) {
            log.debug("Health check request - bypassing gateway verification");
            chain.doFilter(request, response);
            return;
        }

        // Gateway header kontrolü
        if (!GATEWAY_HEADER_VALUE.equals(gatewayHeader)) {
            log.warn("⚠️ SECURITY ALERT: Direct service access attempt blocked! " +
                    "URI: {}, Method: {}, IP: {}, Gateway Header: {}",
                    requestURI, method, httpRequest.getRemoteAddr(), gatewayHeader);

            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            String errorMessage = String.format(
                "{\"error\": \"Forbidden\", " +
                "\"message\": \"Direct service access is not allowed. Please use API Gateway.\", " +
                "\"status\": 403, " +
                "\"path\": \"%s\", " +
                "\"timestamp\": \"%s\"}", 
                requestURI, 
                java.time.Instant.now()
            );
            
            httpResponse.getWriter().write(errorMessage);
            return;
        }

        // Gateway'den gelen geçerli istek
        log.debug("✅ Valid gateway request - URI: {}, Method: {}", requestURI, method);
        chain.doFilter(request, response);
    }
}

