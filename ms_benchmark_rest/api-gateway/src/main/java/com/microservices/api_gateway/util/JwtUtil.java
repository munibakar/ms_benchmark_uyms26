package com.microservices.api_gateway.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Utility Class
 * JWT token validation için kullanılır
 * Secret key Config Server'dan alınır (hard-coded değil)
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer:netflix-clone-auth}")
    private String issuer;

    @Value("${app.jwt.audience:netflix-clone-users}")
    private String audience;

    /**
     * JWT secret'tan signing key oluşturur
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT token'ı validate eder ve claims döndürür
     * @param token JWT token
     * @return Claims
     * @throws Exception Invalid token durumunda
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * JWT token'ın geçerli olup olmadığını kontrol eder
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT token'dan email bilgisini çıkarır
     * @param token JWT token
     * @return email
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JWT token'dan user ID bilgisini çıkarır
     * @param token JWT token
     * @return user ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.get("userId", String.class);
        } catch (Exception e) {
            log.error("Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
    }
}


