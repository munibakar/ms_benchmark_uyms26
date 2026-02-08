package com.microservice.user_service.client;

import com.microservice.user_service.config.FeignClientConfiguration;
import com.microservice.user_service.dto.response.UserDashboardResponse.SubscriptionInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Subscription Service Feign Client
 * Subscription Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(name = "subscription-service", url = "${application.config.subscription-service-url:http://subscription-and-billing-service:9400}", path = "/api/subscription", configuration = FeignClientConfiguration.class)
public interface SubscriptionServiceClient {

    /**
     * Kullanıcının aktif aboneliğini getir
     */
    @GetMapping("/my-subscription")
    SubscriptionInfo getActiveSubscription(@RequestHeader("X-User-Id") String userId);
}
