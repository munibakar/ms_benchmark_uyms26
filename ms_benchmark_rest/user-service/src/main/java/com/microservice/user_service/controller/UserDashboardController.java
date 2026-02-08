package com.microservice.user_service.controller;

import com.microservice.user_service.client.ContentServiceClient;
import com.microservice.user_service.client.PaymentServiceClient;
import com.microservice.user_service.client.ProfileServiceClient;
import com.microservice.user_service.client.SubscriptionServiceClient;
import com.microservice.user_service.dto.response.UserDashboardResponse;
import com.microservice.user_service.dto.response.UserDashboardResponse.*;
import com.microservice.user_service.entity.UserProfile;
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
 * User Dashboard Controller - Service Chain Test Endpoint
 * 
 * Bu controller mikroservisler arası iletişim performansını test etmek için
 * oluşturulmuştur.
 * Tek bir endpoint çağrısı ile 5 farklı servise HTTP istekleri gönderir:
 * 1. Profile Service - Kullanıcı profilleri
 * 2. Subscription Service - Aktif abonelik
 * 3. Payment Service - Son ödemeler
 * 4. Content Service - Önerilen içerikler
 * 5. Watch History - İzleme geçmişi (mock)
 * 
 * Protocol Comparison: REST vs gRPC vs GraphQL performans karşılaştırması için
 * kullanılır.
 * 
 * IMPROVEMENT: Updated to use execute parallel HTTP calls.
 */
@RestController
@RequestMapping("/api/users/analytics")
public class UserDashboardController {

        private static final Logger log = LoggerFactory.getLogger(UserDashboardController.class);

        private final UserProfileService userProfileService;
        private final ProfileServiceClient profileServiceClient;
        private final SubscriptionServiceClient subscriptionServiceClient;
        private final PaymentServiceClient paymentServiceClient;
        private final ContentServiceClient contentServiceClient;
        private final Executor restExecutor;

        public UserDashboardController(
                        UserProfileService userProfileService,
                        ProfileServiceClient profileServiceClient,
                        SubscriptionServiceClient subscriptionServiceClient,
                        PaymentServiceClient paymentServiceClient,
                        ContentServiceClient contentServiceClient,
                        @Qualifier("restExecutor") Executor restExecutor) {
                this.userProfileService = userProfileService;
                this.profileServiceClient = profileServiceClient;
                this.subscriptionServiceClient = subscriptionServiceClient;
                this.paymentServiceClient = paymentServiceClient;
                this.contentServiceClient = contentServiceClient;
                this.restExecutor = restExecutor;
        }

        /**
         * User Dashboard Endpoint - Service Chain Test
         * 
         * GET /api/user/analytics/dashboard/{userId}
         * 
         * Bu endpoint çağrıldığında backend'de şu akış gerçekleşir:
         * 1. Local user bilgisi alınır
         * 2. Profile Service'e Feign HTTP call (REST: ~80-100ms)
         * 3. Subscription Service'e Feign HTTP call (REST: ~80-100ms)
         * 4. Payment Service'e Feign HTTP call (REST: ~80-100ms)
         * 5. Content Service'e Feign HTTP call (REST: ~80-100ms)
         * 6. Watch History mock data (local: ~5ms)
         * 
         * TOPLAM BEKLENEN SÜRE (REST Parallel): ~150-200ms
         */
        @GetMapping("/dashboard/{userId}")
        public ResponseEntity<UserDashboardResponse> getUserDashboard(
                        @PathVariable String userId,
                        @RequestHeader(value = "X-User-Id", required = false) String headerUserId) {

                long startTime = System.currentTimeMillis();
                log.info("📊 [SERVICE CHAIN TEST - REST PARALLEL] Starting dashboard request for userId: {}", userId);

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
                        log.info("  🚀 Starting parallel REST calls on dedicated executor...");
                        long parallelStartTime = System.currentTimeMillis();

                        // 2. HTTP CALL: Profile Service (Feign REST)
                        CompletableFuture<List<ProfileInfo>> profilesFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<ProfileInfo> profiles = profileServiceClient
                                                                        .getProfilesByAccountId(userId);
                                                        log.info("    ✓ Parallel [REST->Profile] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return profiles;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [REST->Profile] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, restExecutor);

                        // 3. HTTP CALL: Subscription Service (Feign REST)
                        CompletableFuture<SubscriptionInfo> subFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        SubscriptionInfo subscription = subscriptionServiceClient
                                                                        .getActiveSubscription(userId);
                                                        if (subscription != null && subscription.getPlan() != null) {
                                                                subscription.setPlanName(
                                                                                subscription.getPlan().getPlanName());
                                                        }
                                                        log.info("    ✓ Parallel [REST->Subscription] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return subscription;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [REST->Subscription] FAILED: {} - Returning null",
                                                                        e.getMessage());
                                                        return null;
                                                }
                                        }, restExecutor);

                        // 4. HTTP CALL: Payment Service (Feign REST)
                        CompletableFuture<List<PaymentInfo>> paymentsFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<PaymentInfo> allPayments = paymentServiceClient
                                                                        .getRecentPayments(userId);
                                                        // Get last 5 payments
                                                        List<PaymentInfo> filtered = allPayments.stream().limit(5)
                                                                        .collect(Collectors.toList());
                                                        log.info("    ✓ Parallel [REST->Payment] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return filtered;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [REST->Payment] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, restExecutor);

                        // 5. HTTP CALL: Content Service (Feign REST)
                        CompletableFuture<List<ContentInfo>> contentsFuture = CompletableFuture
                                        .supplyAsync(() -> {
                                                try {
                                                        long start = System.currentTimeMillis();
                                                        List<ContentInfo> allContents = contentServiceClient
                                                                        .getAllContents();
                                                        // Get first 10 as recommended
                                                        List<ContentInfo> filtered = allContents.stream().limit(100)
                                                                        .collect(Collectors.toList());
                                                        log.info("    ✓ Parallel [REST->Content] - {}ms",
                                                                        System.currentTimeMillis() - start);
                                                        return filtered;
                                                } catch (Exception e) {
                                                        log.warn("    ⚠ Parallel [REST->Content] FAILED: {} - Returning empty list",
                                                                        e.getMessage());
                                                        return new ArrayList<>();
                                                }
                                        }, restExecutor);

                        // Wait for all to complete
                        CompletableFuture
                                        .allOf(profilesFuture, subFuture, paymentsFuture, contentsFuture).join();

                        List<ProfileInfo> profiles = profilesFuture.get();
                        SubscriptionInfo subscription = subFuture.get();
                        List<PaymentInfo> payments = paymentsFuture.get();
                        List<ContentInfo> recommendedContents = contentsFuture.get();

                        log.info("  🚀 All parallel calls completed in {}ms (Truly Parallelized)",
                                        System.currentTimeMillis() - parallelStartTime);

                        // 6. LOCAL: Mock watch history (no external service)
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
                        log.info("📊 [SERVICE CHAIN TEST - REST PARALLEL] ✅ Dashboard request completed - TOTAL TIME: {}ms",
                                        totalTime);
                        log.info("   Protocol: REST PARALLEL | Service Calls: 4 HTTP ||");

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        long totalTime = System.currentTimeMillis() - startTime;
                        log.error("📊 [SERVICE CHAIN TEST - REST PARALLEL] ❌ Dashboard request FAILED - {}ms - Error: {}",
                                        totalTime, e.getMessage(), e);
                        throw new RuntimeException("Failed to get user dashboard: " + e.getMessage(), e);
                }
        }

        /**
         * Health check endpoint
         */
        @GetMapping("/health")
        public ResponseEntity<String> health() {
                return ResponseEntity.ok("User Analytics Service is running - Service Chain Test Ready (Parallel)");
        }
}
