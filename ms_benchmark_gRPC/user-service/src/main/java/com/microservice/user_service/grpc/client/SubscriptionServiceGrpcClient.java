package com.microservice.user_service.grpc.client;

import com.microservice.user_service.dto.response.UserDashboardResponse.SubscriptionInfo;
import com.microservice.user_service.grpc.proto.GetActiveSubscriptionRequest;
import com.microservice.user_service.grpc.proto.SubscriptionGrpcServiceGrpc;
import com.microservice.user_service.grpc.proto.SubscriptionResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * gRPC Client for Subscription Service
 * 
 * REAL gRPC implementation using @GrpcClient stub
 */
@Service
public class SubscriptionServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceGrpcClient.class);

    @GrpcClient("subscription-service")
    private SubscriptionGrpcServiceGrpc.SubscriptionGrpcServiceBlockingStub subscriptionStub;

    /**
     * gRPC üzerinden kullanıcının aktif aboneliğini getir
     */
    public SubscriptionInfo getActiveSubscription(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("gRPC Client: Getting subscription for userId: {} (REAL gRPC CALL)", userId);

        try {
            // REAL gRPC stub call
            GetActiveSubscriptionRequest request = GetActiveSubscriptionRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            SubscriptionResponse response = subscriptionStub.getActiveSubscription(request);

            // gRPC response'u DTO'ya dönüştür
            SubscriptionInfo subscription = SubscriptionInfo.builder()
                    .id(response.getId())
                    .planName(response.getPlan().getPlanName())
                    .status(response.getStatus())
                    .billingCycle(response.getBillingCycle())
                    .build();

            long duration = System.currentTimeMillis() - startTime;
            log.info("gRPC Client: Subscription retrieved successfully - {}ms (REAL gRPC)", duration);

            return subscription;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("gRPC Client: Failed after {}ms - {}", duration, e.getMessage());
            throw new RuntimeException("Failed to get subscription via gRPC", e);
        }
    }
}
