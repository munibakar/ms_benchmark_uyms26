package com.microservices.video_streaming_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Video Streaming Service Application
 * Netflix klonu için video streaming servisi
 * 
 * @EnableDiscoveryClient: Eureka Server'a servis kaydını aktif eder
 */
@SpringBootApplication
@EnableDiscoveryClient
public class VideoStreamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamingServiceApplication.class, args);
	}

}
