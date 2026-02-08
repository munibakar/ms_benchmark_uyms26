package com.microservices.profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Profile Service Application
 * Netflix klonu için profil yönetimi servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 * @EnableFeignClients: OpenFeign client kullanımını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileServiceApplication.class, args);
	}

}
