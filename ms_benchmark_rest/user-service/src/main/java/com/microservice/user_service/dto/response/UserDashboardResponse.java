package com.microservice.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User Dashboard Response DTO
 * Service Chain test için user dashboard bilgilerini döndüren response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {

    private UserInfo user;
    private List<ProfileInfo> profiles;
    private SubscriptionInfo subscription;
    private List<PaymentInfo> recentPayments;
    private List<ContentInfo> recommendedContents;
    private WatchHistoryInfo watchHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String email;
        private String firstName;
        private String lastName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfo {
        private Long id;
        private String profileName;
        private String avatarUrl;
        private Boolean isChildProfile;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionInfo {
        private Long id;
        private String planName; // Keep for backward compatibility/controller mapping
        private String status;
        private String billingCycle;
        private PlanInfo plan; // Matches service side: SubscriptionResponse.plan

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PlanInfo {
            private String planName;
            private Double price;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private Long id;
        private Double amount;
        private String paymentStatus; // Matches service side: BillingHistoryResponse.paymentStatus
        private String paymentDate;

        // Helper for status backward compatibility if needed in UI
        public String getStatus() {
            return paymentStatus;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentInfo {
        private Long id;
        private String title;
        private String contentType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WatchHistoryInfo {
        private Integer totalWatched;
        private Integer recentWatchCount;
        private String lastWatchedDate;
    }
}
