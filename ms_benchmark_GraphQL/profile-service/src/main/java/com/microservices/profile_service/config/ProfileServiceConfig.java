package com.microservices.profile_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Profile Service Configuration Properties
 * Hard-coded değerleri config server'dan okur
 * 
 * Not: maxProfiles artık subscription service'ten alınıyor,
 * burada sadece fallback ve diğer konfigürasyonlar tutuluyor
 */
@Configuration
public class ProfileServiceConfig {

    @Value("${profile.pin.min-length:4}")
    private Integer pinMinLength;

    @Value("${profile.pin.max-length:8}")
    private Integer pinMaxLength;

    @Value("${profile.default.language:tr}")
    private String defaultLanguage;

    @Value("${profile.default.maturity-level:ALL}")
    private String defaultMaturityLevel;

    @Value("${profile.subscription.active-status:ACTIVE}")
    private String subscriptionActiveStatus;

    @Value("${profile.default.max-profiles:5}")
    private Integer defaultMaxProfiles;

    public Integer getPinMinLength() {
        return pinMinLength;
    }

    public Integer getPinMaxLength() {
        return pinMaxLength;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultMaturityLevel() {
        return defaultMaturityLevel;
    }

    public String getSubscriptionActiveStatus() {
        return subscriptionActiveStatus;
    }

    public Integer getDefaultMaxProfiles() {
        return defaultMaxProfiles;
    }
}
