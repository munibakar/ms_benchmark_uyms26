package com.microservices.profile_service.grpc;

import com.microservices.profile_service.dto.response.SubscriptionResponse;
import com.microservices.profile_service.grpc.proto.GetActiveSubscriptionRequest;
import com.microservices.profile_service.grpc.proto.SubscriptionGrpcServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * gRPC Client for Subscription Service
 * Subscription Service'e gRPC üzerinden bağlanır ve abonelik işlemlerini
 * gerçekleştirir
 */
@Service
public class SubscriptionServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceGrpcClient.class);

    @GrpcClient("subscription-service")
    private SubscriptionGrpcServiceGrpc.SubscriptionGrpcServiceBlockingStub subscriptionStub;

    /**
     * gRPC üzerinden kullanıcının aktif aboneliğini getir
     * 
     * @param userId Kullanıcı ID
     * @return SubscriptionResponse
     */
    public SubscriptionResponse getActiveSubscription(String userId) {
        log.info("gRPC Client: Getting active subscription for userId: {}", userId);

        try {
            // gRPC request oluştur
            GetActiveSubscriptionRequest request = GetActiveSubscriptionRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            // gRPC çağrısı yap
            com.microservices.profile_service.grpc.proto.SubscriptionResponse grpcResponse = subscriptionStub
                    .getActiveSubscription(request);

            log.info("gRPC Client: Active subscription retrieved successfully for userId: {}", userId);

            // Plan response'u oluştur
            com.microservices.profile_service.grpc.proto.SubscriptionPlanResponse grpcPlan = grpcResponse.getPlan();
            SubscriptionResponse.SubscriptionPlanResponse planResponse = SubscriptionResponse.SubscriptionPlanResponse
                    .builder()
                    .id(String.valueOf(grpcPlan.getId()))
                    .planName(grpcPlan.getPlanName())
                    .displayName(grpcPlan.getDisplayName())
                    .description(grpcPlan.getDescription())
                    .monthlyPrice(
                            new BigDecimal(grpcPlan.getMonthlyPrice().isEmpty() ? "0" : grpcPlan.getMonthlyPrice()))
                    .yearlyPrice(new BigDecimal(grpcPlan.getYearlyPrice().isEmpty() ? "0" : grpcPlan.getYearlyPrice()))
                    .maxScreens(grpcPlan.getMaxScreens())
                    .maxProfiles(grpcPlan.getMaxProfiles())
                    .videoQuality(grpcPlan.getVideoQuality())
                    .downloadAvailable(grpcPlan.getDownloadAvailable())
                    .adsIncluded(grpcPlan.getAdsIncluded())
                    .isActive(grpcPlan.getIsActive())
                    .build();

            // gRPC response'u DTO'ya dönüştür
            return SubscriptionResponse.builder()
                    .id(String.valueOf(grpcResponse.getId()))
                    .userId(grpcResponse.getUserId())
                    .plan(planResponse)
                    .status(grpcResponse.getStatus())
                    .billingCycle(grpcResponse.getBillingCycle())
                    .autoRenew(grpcResponse.getAutoRenew())
                    .build();

        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Failed to get active subscription for userId: {}. Status: {}",
                    userId, e.getStatus(), e);
            throw new RuntimeException("Failed to get active subscription via gRPC: " + e.getStatus().getDescription(),
                    e);
        }
    }
}
