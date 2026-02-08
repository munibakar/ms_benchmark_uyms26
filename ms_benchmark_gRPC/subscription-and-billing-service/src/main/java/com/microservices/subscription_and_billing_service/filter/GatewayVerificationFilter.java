package com.microservices.subscription_and_billing_service.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gateway Verification Filter
 * Sadece API Gateway'den gelen istekleri kabul eder
 */
@Component
@Order(1)
public class GatewayVerificationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GatewayVerificationFilter.class);
    private static final String GATEWAY_HEADER = "X-Gateway-Request";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String gatewayHeader = httpRequest.getHeader(GATEWAY_HEADER);
        String requestURI = httpRequest.getRequestURI();

        // Actuator endpoint'lerini ve Custom Health check'i bypass et
        if (requestURI.startsWith("/actuator") || requestURI.equals("/api/subscription/health")) {
            chain.doFilter(request, response);
            return;
        }

        // Gateway header kontrolü
        if (!"true".equals(gatewayHeader)) {
            log.warn("Blocked request without gateway header: {} from IP: {}", 
                    requestURI, httpRequest.getRemoteAddr());
            
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"Direct access not allowed. Please use API Gateway.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}




