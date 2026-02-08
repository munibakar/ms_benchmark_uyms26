package com.microservice.user_service.client;

import com.microservice.user_service.config.FeignClientConfiguration;
import com.microservice.user_service.dto.response.UserDashboardResponse.ContentInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Content Service Feign Client
 * Content Management Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(name = "content-service", url = "${application.config.content-service-url:http://content-management-service:9200}", path = "/api/contents", configuration = FeignClientConfiguration.class)
public interface ContentServiceClient {

    /**
     * Önerilen içerikleri getir (first 10)
     */
    @GetMapping
    List<ContentInfo> getAllContents();
}
