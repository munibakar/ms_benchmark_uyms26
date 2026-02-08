package com.microservices.subscription_and_billing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Subscription and Billing Service Application
 * Netflix klonu için abonelik ve fatura yönetimi servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SubscriptionAndBillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionAndBillingServiceApplication.class, args);
	}

}
