package com.microservices.video_streaming_service.controller;

import com.microservices.video_streaming_service.client.ContentServiceGraphQLClient;
import com.microservices.video_streaming_service.client.SubscriptionServiceGraphQLClient;
import com.microservices.video_streaming_service.dto.response.ContentResponse;
import com.microservices.video_streaming_service.dto.response.SubscriptionResponse;
import com.microservices.video_streaming_service.exception.ResourceNotFoundException;
import com.microservices.video_streaming_service.exception.SubscriptionRequiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Video Streaming Service - GraphQL Controller
 * Video metadata sorguları için GraphQL endpoint'leri
 * NOT: Gerçek video streaming REST API üzerinden yapılır
 */
@Controller
public class VideoStreamingGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(VideoStreamingGraphQLController.class);

    private final ContentServiceGraphQLClient contentServiceGraphQLClient;
    private final SubscriptionServiceGraphQLClient subscriptionServiceGraphQLClient;

    public VideoStreamingGraphQLController(ContentServiceGraphQLClient contentServiceGraphQLClient,
                                            SubscriptionServiceGraphQLClient subscriptionServiceGraphQLClient) {
        this.contentServiceGraphQLClient = contentServiceGraphQLClient;
        this.subscriptionServiceGraphQLClient = subscriptionServiceGraphQLClient;
    }

    /**
     * GraphQL Query: getContentStreamingInfo
     * Content streaming bilgisi getir (abonelik kontrolü ile)
     */
    @QueryMapping
    public StreamingInfo getContentStreamingInfo(@Argument Long contentId, @Argument String userId) {
        log.info("GraphQL Query: getContentStreamingInfo for contentId: {}, userId: {}", contentId, userId);

        // Abonelik kontrolü
        checkSubscription(userId);

        // Content bilgisi al
        ContentResponse content = contentServiceGraphQLClient.getContentById(contentId);
        
        if (content == null) {
            throw new ResourceNotFoundException("Content not found with id: " + contentId);
        }

        if (!content.getIsActive()) {
            throw new ResourceNotFoundException("Content is not active: " + contentId);
        }

        return new StreamingInfo(
                content.getId(),
                content.getTitle(),
                content.getVideoFilePath(),
                content.getContentType(),
                content.getIsActive(),
                "/api/stream/content/" + contentId
        );
    }

    /**
     * GraphQL Query: getEpisodeStreamingInfo
     * Episode streaming bilgisi getir (abonelik kontrolü ile)
     */
    @QueryMapping
    public EpisodeStreamingInfo getEpisodeStreamingInfo(@Argument Long episodeId, @Argument String userId) {
        log.info("GraphQL Query: getEpisodeStreamingInfo for episodeId: {}, userId: {}", episodeId, userId);

        // Abonelik kontrolü
        checkSubscription(userId);

        // Episode bilgisi al
        ContentServiceGraphQLClient.EpisodeResponse episode = contentServiceGraphQLClient.getEpisodeById(episodeId);
        
        if (episode == null) {
            throw new ResourceNotFoundException("Episode not found with id: " + episodeId);
        }

        if (!episode.getIsActive()) {
            throw new ResourceNotFoundException("Episode is not active: " + episodeId);
        }

        return new EpisodeStreamingInfo(
                episode.getId(),
                episode.getTitle(),
                episode.getVideoFilePath(),
                episode.getIsActive(),
                "/api/stream/episode/" + episodeId
        );
    }

    /**
     * GraphQL Query: checkSubscriptionStatus
     * Kullanıcının abonelik durumunu kontrol et
     */
    @QueryMapping
    public SubscriptionStatus checkSubscriptionStatus(@Argument String userId) {
        log.info("GraphQL Query: checkSubscriptionStatus for userId: {}", userId);

        try {
            SubscriptionResponse subscription = subscriptionServiceGraphQLClient.getActiveSubscription(userId);
            
            boolean isActive = subscription != null && "ACTIVE".equals(subscription.getStatus());
            
            return new SubscriptionStatus(
                    isActive,
                    subscription != null ? subscription.getStatus() : null,
                    userId
            );
        } catch (Exception e) {
            log.warn("Failed to get subscription status for userId: {}, error: {}", userId, e.getMessage());
            return new SubscriptionStatus(false, null, userId);
        }
    }

    /**
     * Abonelik kontrolü
     */
    private void checkSubscription(String userId) {
        try {
            SubscriptionResponse subscription = subscriptionServiceGraphQLClient.getActiveSubscription(userId);
            
            if (subscription == null || !"ACTIVE".equals(subscription.getStatus())) {
                throw new SubscriptionRequiredException("Active subscription required to stream content");
            }
        } catch (SubscriptionRequiredException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Subscription check failed for userId: {}, allowing access. Error: {}", userId, e.getMessage());
            // Subscription service hatası durumunda erişime izin ver (graceful degradation)
        }
    }

    // ============== Response Records ==============

    public record StreamingInfo(
            Long contentId,
            String title,
            String videoFilePath,
            String contentType,
            Boolean isActive,
            String streamingUrl) {}

    public record EpisodeStreamingInfo(
            Long episodeId,
            String title,
            String videoFilePath,
            Boolean isActive,
            String streamingUrl) {}

    public record SubscriptionStatus(
            Boolean hasActiveSubscription,
            String status,
            String userId) {}
}
