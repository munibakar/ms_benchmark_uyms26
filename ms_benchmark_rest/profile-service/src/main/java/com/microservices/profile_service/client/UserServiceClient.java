package com.microservices.profile_service.client;

import com.microservices.profile_service.config.FeignClientConfiguration;
import com.microservices.profile_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Feign Client
 * Profile Service'ten User Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "user-service", 
    url = "${application.config.user-service-url:http://user-service:9000}",
    path = "/api/users",
    configuration = FeignClientConfiguration.class
)
public interface UserServiceClient {

    /**
     * User ID'ye göre kullanıcı profilini getir
     */
    @GetMapping("/profile/{userId}")
    UserProfileResponse getUserProfile(@PathVariable("userId") String userId);
}
