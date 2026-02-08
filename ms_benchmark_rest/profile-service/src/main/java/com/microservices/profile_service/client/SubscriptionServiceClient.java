package com.microservices.profile_service.client;

import com.microservices.profile_service.config.FeignClientConfiguration;
import com.microservices.profile_service.dto.response.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Subscription Service Feign Client
 * Profile Service'ten Subscription Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "subscription-and-billing-service", 
    url = "${application.config.subscription-service-url:http://subscription-and-billing-service:9100}",
    path = "/api/subscription",
    configuration = FeignClientConfiguration.class
)
public interface SubscriptionServiceClient {

    /**
     * User ID'ye göre aktif aboneliği getir
     * Not: Subscription service'te userId header'dan alınıyor
     */
    @GetMapping("/my-subscription")
    SubscriptionResponse getActiveSubscription(@RequestHeader("X-User-Id") String userId);
}
