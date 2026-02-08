package com.microservices.profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Profile Service Application
 * Netflix klonu için profil yönetimi servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
