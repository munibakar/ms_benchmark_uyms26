package com.microservice.user_service.client;

import com.microservice.user_service.config.FeignClientConfiguration;
import com.microservice.user_service.dto.response.UserDashboardResponse.ProfileInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Profile Service Feign Client
 * Profile Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(name = "profile-service", url = "${application.config.profile-service-url:http://profile-service:9300}", path = "/api/profiles", configuration = FeignClientConfiguration.class)
public interface ProfileServiceClient {

    /**
     * Kullanıcının tüm profillerini getir
     */
    @GetMapping("/account/{accountId}")
    List<ProfileInfo> getProfilesByAccountId(@PathVariable("accountId") String accountId);
}
