package com.authentication.microservices.authentication.client;

import com.authentication.microservices.authentication.config.FeignClientConfiguration;
import com.authentication.microservices.authentication.dto.request.CreateUserProfileRequest;
import com.authentication.microservices.authentication.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * User Service Feign Client
 * Auth Service'ten User Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "user-service", 
    url = "${application.config.user-service-url:http://user-service:9000}",
    path = "/api/users",
    configuration = FeignClientConfiguration.class
)
public interface UserServiceClient {

    /**
     * User Service'te yeni bir kullanıcı profili oluştur
     */
    @PostMapping("/profile")
    UserProfileResponse createUserProfile(@RequestBody CreateUserProfileRequest request);
}

