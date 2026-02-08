package com.microservices.profile_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration
 * JPA Auditing'i aktif eder (CreatedDate, LastModifiedDate için)
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
