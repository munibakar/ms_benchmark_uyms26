package com.microservices.video_streaming_service.client;

import com.microservices.video_streaming_service.config.FeignClientConfiguration;
import com.microservices.video_streaming_service.dto.response.ContentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Content Management Service Feign Client
 * Video Streaming Service'ten Content Management Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "content-management-service", 
    url = "${application.config.content-service-url:http://content-management-service:9200}",
    path = "/api/contents",
    configuration = FeignClientConfiguration.class
)
public interface ContentManagementServiceClient {

    /**
     * Content ID'ye göre içerik bilgilerini getir (videoFilePath için)
     */
    @GetMapping("/{contentId}")
    ContentResponse getContentById(@PathVariable("contentId") Long contentId);
}






