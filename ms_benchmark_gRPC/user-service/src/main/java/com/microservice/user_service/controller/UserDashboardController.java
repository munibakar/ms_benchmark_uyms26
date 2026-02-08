package com.microservice.user_service.controller;

import com.microservice.user_service.dto.response.UserDashboardResponse;
import com.microservice.user_service.dto.response.UserDashboardResponse.*;
import com.microservice.user_service.entity.UserProfile;
import com.microservice.user_service.grpc.client.ContentServiceGrpcClient;
import com.microservice.user_service.grpc.client.PaymentServiceGrpcClient;
import com.microservice.user_service.grpc.client.ProfileServiceGrpcClient;
import com.microservice.user_service.grpc.client.SubscriptionServiceGrpcClient;
import com.microservice.user_service.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * User Dashboard Controller - Service Chain Test Endpoint (gRPC VERSION)
 * 
 * Optimized with dedicated thread pool for true parallel execution.
 */
@RestController
@RequestMapping("/api/users/analytics")
public class UserDashboardController {

        private static final Logger log = LoggerFactory.getLogger(UserDashboardController.class);

        private final UserProfileService userProfileService;
        private final ProfileServiceGrpcClient profileServiceGrpcClient;
        private final SubscriptionServiceGrpcClient subscriptionServiceGrpcClient;
        private final PaymentServiceGrpcClient paymentServiceGrpcClient;
        private final ContentServiceGrpcClient contentServiceGrpcClient;
        private final Executor grpcExecutor;

        public UserDashboardController(
                        UserProfileService userProfileService,
                        ProfileServiceGrpcClient profileServiceGrpcClient,
                        SubscriptionServiceGrpcClient subscriptionServiceGrpcClient,
                        PaymentServiceGrpcClient paymentServiceGrpcClient,
                        ContentServiceGrpcClient contentServiceGrpcClient,
                        @Qualifier("grpcExecutor") Executor grpcExecutor) {
                this.userProfileService = userProfileService;
                this.profileServiceGrpcClient = profileServiceGrpcClient;
                this.subscriptionServiceGrpcClient = subscriptionServiceGrpcClient;
                this.paymentServiceGrpcClient = paymentServiceGrpcClient;
                this.contentServiceGrpcClient = contentServiceGrpcClient;
                this.grpcExecutor = grpcExecutor;
        }

        /**
         * User Dashboard Endpoint - Service Chain Test (gRPC)
         */
        @GetMapping("/dashboard/{userId}")
        public ResponseEntity<UserDashboardResponse> getUserDashboard(
                        @PathVariable String userId,
                        @RequestHeader(value = "X-User-Id", required = false) String headerUserId) {

                long startTime = System.currentTimeMillis();
                log.info("📊 [SERVICE CHAIN TEST - gRPC] Starting dashboard request for userId: {}", userId);

                try {
                        // 1. LOCAL: Get user info from database
                        long step1Start = System.currentTimeMillis();
                        com.microservice.user_service.dto.response.UserProfileResponse userProfileResponse = userProfileService
                                        .getUserProfileByUserId(userId);
                        log.info("  ✓ Step 1 [LOCAL] User info retrieved - {}ms",
                                        System.currentTimeMillis() - step1Start);

                        UserInfo userInfo = UserInfo.builder()
                                        .userId(userProfileResponse.getUserId())
                                        .email(userProfileResponse.getEmail())
                                        .firstName(userProfileResponse.getFirstName())
                                        .lastName(userProfileResponse.getLastName())
                                        .build();

                        // PARALLEL EXECUTION START
                        log.info("  🚀 Starting parallel gRPC calls on dedicated executor...");
                        long parallelStartTime = System.currentTimeMillis();

                        // 2. gRPC CALL: Profile Service
                        CompletableFuture<List<ProfileInfo>> profilesFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<ProfileInfo> profiles = profileServiceGrpcClient
                                                                        .getProfilesByAccountId(userId);
                                                        log.info("    ✓ Parallel [gRPC->Profile] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return profiles;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [gRPC->Profile] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, grpcExecutor);

                        // 3. gRPC CALL: Subscription Service
                        CompletableFuture<SubscriptionInfo> subFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        SubscriptionInfo sub = subscriptionServiceGrpcClient
                                                                        .getActiveSubscription(userId);
                                                        log.info("    ✓ Parallel [gRPC->Subscription] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return sub;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [gRPC->Subscription] FAILED: {} - Returning null",
                                                                        e.getMessage());
                                                        return null;
                                                }
                                        }, grpcExecutor);

                        // 4. gRPC CALL: Payment Service
                        CompletableFuture<List<PaymentInfo>> paymentsFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<PaymentInfo> allPayments = paymentServiceGrpcClient
                                                                        .getRecentPayments(userId);
                                                        List<PaymentInfo> filtered = allPayments.stream().limit(5)
                                                                        .collect(Collectors.toList());
                                                        log.info("    ✓ Parallel [gRPC->Payment] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return filtered;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [gRPC->Payment] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, grpcExecutor);

                        // 5. gRPC CALL: Content Service
                        CompletableFuture<List<ContentInfo>> contentsFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<ContentInfo> allContents = contentServiceGrpcClient
                                                                        .getAllContents();
                                                        List<ContentInfo> filtered = allContents.stream().limit(100)
                                                                        .collect(Collectors.toList());
                                                        log.info("    ✓ Parallel [gRPC->Content] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return filtered;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [gRPC->Content] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, grpcExecutor);

                        // Wait for all to complete
                        CompletableFuture
                                        .allOf(profilesFuture, subFuture, paymentsFuture, contentsFuture).join();

                        List<ProfileInfo> profiles = profilesFuture.get();
                        SubscriptionInfo subscription = subFuture.get();
                        List<PaymentInfo> payments = paymentsFuture.get();
                        List<ContentInfo> recommendedContents = contentsFuture.get();

                        log.info("  🚀 All parallel calls completed in {}ms (Truly Parallelized)",
                                        System.currentTimeMillis() - parallelStartTime);

                        // 6. LOCAL: Mock watch history
                        long step6Start = System.currentTimeMillis();
                        WatchHistoryInfo watchHistory = WatchHistoryInfo.builder()
                                        .totalWatched(42)
                                        .recentWatchCount(12)
                                        .lastWatchedDate(LocalDateTime.now().toString())
                                        .build();
                        log.info("  ✓ Step 6 [LOCAL] Watch history created - {}ms",
                                        System.currentTimeMillis() - step6Start);

                        // Build final response
                        UserDashboardResponse response = UserDashboardResponse.builder()
                                        .user(userInfo)
                                        .profiles(profiles)
                                        .subscription(subscription)
                                        .recentPayments(payments)
                                        .recommendedContents(recommendedContents)
                                        .watchHistory(watchHistory)
                                        .build();

                        long totalTime = System.currentTimeMillis() - startTime;
                        log.info("📊 [SERVICE CHAIN TEST - gRPC OPTIMIZED v2] ✅ Dashboard request completed - TOTAL TIME: {}ms",
                                        totalTime);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        long totalTime = System.currentTimeMillis() - startTime;
                        log.error("📊 [SERVICE CHAIN TEST - gRPC] ❌ Dashboard request FAILED - {}ms - Error: {}",
                                        totalTime, e.getMessage(), e);
                        throw new RuntimeException("Failed to get user dashboard: " + e.getMessage(), e);
                }
        }

        /**
         * Health check endpoint
         */
        @GetMapping("/health")
        public ResponseEntity<String> health() {
                return ResponseEntity.ok("User Analytics Service (gRPC) is running - Optimized Ready");
        }
}
