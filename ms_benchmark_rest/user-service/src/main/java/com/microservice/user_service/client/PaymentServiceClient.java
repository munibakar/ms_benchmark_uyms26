package com.microservice.user_service.client;

import com.microservice.user_service.config.FeignClientConfiguration;
import com.microservice.user_service.dto.response.UserDashboardResponse.PaymentInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Payment Service Feign Client
 * Payment Service'e HTTP istekleri göndermek için kullanılır
 */
@FeignClient(name = "subscription-and-billing-service", url = "${application.config.payment-service-url:http://subscription-and-billing-service:9100}", path = "/api/billing", configuration = FeignClientConfiguration.class)
public interface PaymentServiceClient {

    /**
     * Kullanıcının son ödeme bilgilerini (fatura geçmişini) getir
     */
    @GetMapping("/history")
    List<PaymentInfo> getRecentPayments(@RequestHeader("X-User-Id") String userId);
}
