package com.microservice.user_service.controller;

import com.microservice.user_service.dto.response.UserDashboardResponse;
import com.microservice.user_service.dto.response.UserDashboardResponse.*;
import com.microservice.user_service.dto.response.UserProfileResponse;
import com.microservice.user_service.graphql.client.ContentServiceGraphQLClient;
import com.microservice.user_service.graphql.client.ProfileServiceGraphQLClient;
import com.microservice.user_service.graphql.client.SubscriptionServiceGraphQLClient;
import com.microservice.user_service.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard GraphQL Controller - Service Chain Test
 * 
 * Bu controller REST versiyonundaki UserDashboardController'ın GraphQL
 * karşılığıdır.
 * Tek bir GraphQL query ile 4 farklı servise GraphQL istekleri gönderir:
 * 1. Profile Service - Kullanıcı profilleri
 * 2. Subscription Service - Aktif abonelik
 * 3. Subscription Service - Fatura geçmişi (billingHistory)
 * 4. Content Service - Önerilen içerikler
 * 
 * Protocol Comparison: REST vs gRPC vs GraphQL performans karşılaştırması için
 * kullanılır.
 */
@Controller
public class DashboardGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(DashboardGraphQLController.class);

    private final UserProfileService userProfileService;
    private final ProfileServiceGraphQLClient profileServiceClient;
    private final SubscriptionServiceGraphQLClient subscriptionServiceClient;
    private final ContentServiceGraphQLClient contentServiceClient;

    public DashboardGraphQLController(
            UserProfileService userProfileService,
            ProfileServiceGraphQLClient profileServiceClient,
            SubscriptionServiceGraphQLClient subscriptionServiceClient,
            ContentServiceGraphQLClient contentServiceClient) {
        this.userProfileService = userProfileService;
        this.profileServiceClient = profileServiceClient;
        this.subscriptionServiceClient = subscriptionServiceClient;
        this.contentServiceClient = contentServiceClient;
    }

    /**
     * GraphQL Query: dashboard(userId: String!)
     * 
     * Bu query çağrıldığında backend'de şu akış gerçekleşir:
     * 1. Local user bilgisi alınır
     * 2. Profile Service'e GraphQL call
     * 3. Subscription Service'e GraphQL call (subscription)
     * 4. Subscription Service'e GraphQL call (billingHistory)
     * 5. Content Service'e GraphQL call
     * 6. Watch History mock data (local)
     */
    @QueryMapping
    public UserDashboardResponse dashboard(@Argument String userId) {
        long startTime = System.currentTimeMillis();
        log.info("📊 [SERVICE CHAIN TEST - GraphQL] Starting dashboard request for userId: {}", userId);

        FetchTimeInfo fetchTimes = new FetchTimeInfo();

        try {
            // 1. LOCAL: Get user info from database
            long step1Start = System.currentTimeMillis();
            UserProfileResponse userProfileResponse = userProfileService.getUserProfileByUserId(userId);
            long step1Time = System.currentTimeMillis() - step1Start;
            log.info("  ✓ Step 1 [LOCAL] User info retrieved - {}ms", step1Time);

            UserInfo userInfo = UserInfo.builder()
                    .userId(userProfileResponse.getUserId())
                    .email(userProfileResponse.getEmail())
                    .firstName(userProfileResponse.getFirstName())
                    .lastName(userProfileResponse.getLastName())
                    .build();

            // 2. GraphQL CALL: Profile Service
            long step2Start = System.currentTimeMillis();
            List<ProfileInfo> profiles = new ArrayList<>();
            try {
                profiles = profileServiceClient.getProfilesByAccountId(userId);
                long step2Time = System.currentTimeMillis() - step2Start;
                fetchTimes.setProfiles(step2Time);
                log.info("  ✓ Step 2 [GraphQL->Profile Service] {} profiles retrieved - {}ms",
                        profiles.size(), step2Time);
            } catch (Exception e) {
                fetchTimes.setProfiles(System.currentTimeMillis() - step2Start);
                log.warn("  ⚠ Step 2 [GraphQL->Profile Service] FAILED - Continuing with empty list");
            }

            // 3. GraphQL CALL: Subscription Service (subscription)
            long step3Start = System.currentTimeMillis();
            SubscriptionInfo subscription = null;
            try {
                subscription = subscriptionServiceClient.getActiveSubscription(userId);
                long step3Time = System.currentTimeMillis() - step3Start;
                fetchTimes.setSubscription(step3Time);
                log.info("  ✓ Step 3 [GraphQL->Subscription Service] Subscription retrieved - {}ms", step3Time);
            } catch (Exception e) {
                fetchTimes.setSubscription(System.currentTimeMillis() - step3Start);
                log.warn("  ⚠ Step 3 [GraphQL->Subscription Service] FAILED - Continuing with null");
            }

            // 4. GraphQL CALL: Subscription Service (billing history)
            long step4Start = System.currentTimeMillis();
            List<PaymentInfo> payments = new ArrayList<>();
            try {
                List<PaymentInfo> allPayments = subscriptionServiceClient.getBillingHistory(userId);
                payments = allPayments.stream().limit(5).collect(Collectors.toList());
                long step4Time = System.currentTimeMillis() - step4Start;
                fetchTimes.setBilling(step4Time);
                log.info("  ✓ Step 4 [GraphQL->Subscription Service] {} payments retrieved - {}ms",
                        payments.size(), step4Time);
            } catch (Exception e) {
                fetchTimes.setBilling(System.currentTimeMillis() - step4Start);
                log.warn("  ⚠ Step 4 [GraphQL->Subscription Service] FAILED - Continuing with empty list");
            }

            // 5. GraphQL CALL: Content Service
            long step5Start = System.currentTimeMillis();
            List<ContentInfo> recommendedContents = new ArrayList<>();
            try {
                List<ContentInfo> allContents = contentServiceClient.getAllContents();
                recommendedContents = allContents.stream().limit(100).collect(Collectors.toList());
                long step5Time = System.currentTimeMillis() - step5Start;
                fetchTimes.setContents(step5Time);
                log.info("  ✓ Step 5 [GraphQL->Content Service] {} contents retrieved - {}ms",
                        recommendedContents.size(), step5Time);
            } catch (Exception e) {
                fetchTimes.setContents(System.currentTimeMillis() - step5Start);
                log.warn("  ⚠ Step 5 [GraphQL->Content Service] FAILED - Continuing with empty list");
            }

            // 6. LOCAL: Mock watch history
            WatchHistoryInfo watchHistory = WatchHistoryInfo.builder()
                    .totalWatched(42)
                    .recentWatchCount(12)
                    .lastWatchedDate(LocalDateTime.now().toString())
                    .build();

            long totalTime = System.currentTimeMillis() - startTime;
            fetchTimes.setTotal(totalTime);

            // Build final response
            UserDashboardResponse response = UserDashboardResponse.builder()
                    .user(userInfo)
                    .profiles(profiles)
                    .subscription(subscription)
                    .recentPayments(payments)
                    .recommendedContents(recommendedContents)
                    .watchHistory(watchHistory)
                    .fetchTimeMs(fetchTimes)
                    .build();

            log.info("📊 [SERVICE CHAIN TEST - GraphQL] ✅ Dashboard request completed - TOTAL TIME: {}ms", totalTime);
            log.info("   Protocol: GraphQL | Service Calls: 4 GraphQL + 1 local | User: {}", userId);

            return response;

        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("📊 [SERVICE CHAIN TEST - GraphQL] ❌ Dashboard request FAILED - {}ms - Error: {}",
                    totalTime, e.getMessage(), e);
            throw new RuntimeException("Failed to get user dashboard: " + e.getMessage(), e);
        }
    }
}
