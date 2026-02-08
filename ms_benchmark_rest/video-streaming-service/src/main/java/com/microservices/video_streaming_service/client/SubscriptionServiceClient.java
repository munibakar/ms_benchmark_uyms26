package com.microservices.video_streaming_service.client;

import com.microservices.video_streaming_service.config.FeignClientConfiguration;
import com.microservices.video_streaming_service.dto.response.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Subscription Service Feign Client
 * Video Streaming Service'ten Subscription Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(
    name = "subscription-service", 
    url = "${application.config.subscription-service-url:http://subscription-and-billing-service:9100}",
    path = "/api/subscription",
    configuration = FeignClientConfiguration.class
)
public interface SubscriptionServiceClient {

    /**
     * Kullanıcının aktif aboneliğini kontrol et
     * 
     * @param userId Kullanıcı ID (X-User-Id header'ı subscription service tarafından bekleniyor)
     * @return SubscriptionResponse aktif abonelik bilgisi
     */
    @GetMapping("/my-subscription")
    SubscriptionResponse getActiveSubscription(@RequestHeader("X-User-Id") String userId);
}
