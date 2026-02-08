package com.microservices.content_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Content Management Service Application
 * Netflix klonu için içerik yönetimi servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ContentManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentManagementServiceApplication.class, args);
	}

}
