package com.microservices.subscription_and_billing_service.grpc;

import com.microservices.subscription_and_billing_service.grpc.proto.GetActiveSubscriptionRequest;
import com.microservices.subscription_and_billing_service.grpc.proto.SubscriptionGrpcServiceGrpc;
import com.microservices.subscription_and_billing_service.grpc.proto.SubscriptionPlanResponse;
import com.microservices.subscription_and_billing_service.grpc.proto.SubscriptionResponse;
import com.microservices.subscription_and_billing_service.service.SubscriptionService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC Server Implementation for Subscription Service
 * Profile Service ve Video Streaming Service bu endpoint'leri gRPC üzerinden çağırır
 */
@GrpcService
public class SubscriptionGrpcServiceImpl extends SubscriptionGrpcServiceGrpc.SubscriptionGrpcServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionGrpcServiceImpl.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionGrpcServiceImpl(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * gRPC üzerinden kullanıcının aktif aboneliğini getir
     */
    @Override
    public void getActiveSubscription(
            GetActiveSubscriptionRequest request,
            StreamObserver<SubscriptionResponse> responseObserver) {

        log.info("gRPC: Getting active subscription for userId: {}", request.getUserId());

        try {
            // Mevcut service'i kullan
            com.microservices.subscription_and_billing_service.dto.response.SubscriptionResponse result = 
                    subscriptionService.getActiveSubscription(request.getUserId());

            // DTO response'u gRPC response'a dönüştür
            SubscriptionPlanResponse planResponse = SubscriptionPlanResponse.newBuilder()
                    .setId(result.getPlan().getId())
                    .setPlanName(result.getPlan().getPlanName() != null ? result.getPlan().getPlanName() : "")
                    .setDisplayName(result.getPlan().getDisplayName() != null ? result.getPlan().getDisplayName() : "")
                    .setDescription(result.getPlan().getDescription() != null ? result.getPlan().getDescription() : "")
                    .setMonthlyPrice(result.getPlan().getMonthlyPrice() != null ? result.getPlan().getMonthlyPrice().toString() : "0")
                    .setYearlyPrice(result.getPlan().getYearlyPrice() != null ? result.getPlan().getYearlyPrice().toString() : "0")
                    .setMaxScreens(result.getPlan().getMaxScreens() != null ? result.getPlan().getMaxScreens() : 1)
                    .setMaxProfiles(result.getPlan().getMaxProfiles() != null ? result.getPlan().getMaxProfiles() : 1)
                    .setVideoQuality(result.getPlan().getVideoQuality() != null ? result.getPlan().getVideoQuality() : "SD")
                    .setDownloadAvailable(result.getPlan().getDownloadAvailable() != null ? result.getPlan().getDownloadAvailable() : false)
                    .setAdsIncluded(result.getPlan().getAdsIncluded() != null ? result.getPlan().getAdsIncluded() : true)
                    .setIsActive(result.getPlan().getIsActive() != null ? result.getPlan().getIsActive() : false)
                    .build();

            SubscriptionResponse grpcResponse = SubscriptionResponse.newBuilder()
                    .setId(result.getId())
                    .setUserId(result.getUserId() != null ? result.getUserId() : "")
                    .setPlan(planResponse)
                    .setStatus(result.getStatus() != null ? result.getStatus() : "")
                    .setBillingCycle(result.getBillingCycle() != null ? result.getBillingCycle() : "")
                    .setStartDate(result.getStartDate() != null ? result.getStartDate().toString() : "")
                    .setEndDate(result.getEndDate() != null ? result.getEndDate().toString() : "")
                    .setCancelledAt(result.getCancelledAt() != null ? result.getCancelledAt().toString() : "")
                    .setCancellationReason(result.getCancellationReason() != null ? result.getCancellationReason() : "")
                    .setAutoRenew(result.getAutoRenew() != null ? result.getAutoRenew() : false)
                    .setNextBillingDate(result.getNextBillingDate() != null ? result.getNextBillingDate().toString() : "")
                    .setCreatedAt(result.getCreatedAt() != null ? result.getCreatedAt().toString() : "")
                    .setUpdatedAt(result.getUpdatedAt() != null ? result.getUpdatedAt().toString() : "")
                    .build();

            log.info("gRPC: Active subscription retrieved successfully for userId: {}", request.getUserId());

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC: Failed to get active subscription for userId: {}", request.getUserId(), e);
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription("Active subscription not found: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}
