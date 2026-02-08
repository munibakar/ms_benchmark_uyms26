package com.microservices.video_streaming_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Video Streaming Service Application
 * Netflix klonu için video streaming servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 * @EnableFeignClients: OpenFeign client kullanımını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class VideoStreamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamingServiceApplication.class, args);
	}

}
