package com.authentication.microservices.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Authentication Service Application
 * 
 * @EnableJpaAuditing: JPA Auditing'i aktif eder
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class AuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

}
