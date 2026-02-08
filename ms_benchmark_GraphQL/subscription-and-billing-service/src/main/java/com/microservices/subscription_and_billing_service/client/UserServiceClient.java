package com.microservices.subscription_and_billing_service.client;

import com.microservices.subscription_and_billing_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Feign Client
 * Subscription Service'ten User Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "user-service",
    url = "${application.config.user-service-url:http://user-service:9000}",
    path = "/api/users"
)
public interface UserServiceClient {

    /**
     * User ID'ye göre kullanıcı profilini getir
     */
    @GetMapping("/profile/{userId}")
    UserProfileResponse getUserProfile(@PathVariable("userId") String userId);
}




