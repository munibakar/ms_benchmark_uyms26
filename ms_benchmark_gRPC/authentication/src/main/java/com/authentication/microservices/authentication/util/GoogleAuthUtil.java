package com.authentication.microservices.authentication.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Google Authentication Utility - Authentication Microservice
 */
@Component
public class GoogleAuthUtil {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthUtil.class);

    @Value("${app.google.client-id}")
    private String googleClientId;

    /**
     * Verify Google ID token and extract user information
     */
    public GoogleUserInfo verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            
            if (googleIdToken != null) {
                Payload payload = googleIdToken.getPayload();
                
                return GoogleUserInfo.builder()
                        .email(payload.getEmail())
                        .givenName((String) payload.get("given_name"))
                        .familyName((String) payload.get("family_name"))
                        .picture((String) payload.get("picture"))
                        .build();
            } else {
                throw new RuntimeException("Invalid Google token");
            }
        } catch (Exception e) {
            log.error("Error verifying Google token", e);
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }

    /**
     * Google User Info DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class GoogleUserInfo {
        private String email;
        private String givenName;
        private String familyName;
        private String picture;
    }
}

