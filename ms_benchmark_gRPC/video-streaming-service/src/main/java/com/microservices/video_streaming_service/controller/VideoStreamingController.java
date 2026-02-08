package com.microservices.video_streaming_service.controller;

import com.microservices.video_streaming_service.service.VideoStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Video Streaming Controller - Video Streaming Service
 * Video streaming işlemlerini yöneten REST API
 */
@RestController
@RequestMapping("/api/stream")
public class VideoStreamingController {

    private static final Logger log = LoggerFactory.getLogger(VideoStreamingController.class);

    private final VideoStreamingService videoStreamingService;

    public VideoStreamingController(VideoStreamingService videoStreamingService) {
        this.videoStreamingService = videoStreamingService;
    }

    /**
     * Health check endpoint
     * GET /api/stream/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Video Streaming Service is running");
    }

    /**
     * Content ID'ye göre videoyu stream et
     * HTTP Range Request desteği ile (206 Partial Content)
     * 
     * GET /api/stream/content/{contentId}
     * 
     * Headers:
     *   Range: bytes=0-1023 (optional)
     *   X-User-Id: user id (required, added by API Gateway)
     */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<Resource> streamContent(
            @PathVariable Long contentId,
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        
        log.info("Received request to stream content: contentId={}, userId={}, range={}", contentId, userId, rangeHeader);
        
        return videoStreamingService.streamContent(contentId, userId, rangeHeader);
    }

    /**
     * Episode ID'ye göre videoyu stream et
     * HTTP Range Request desteği ile (206 Partial Content)
     * 
     * GET /api/stream/episode/{episodeId}
     * 
     * Headers:
     *   Range: bytes=0-1023 (optional)
     *   X-User-Id: user id (required, added by API Gateway)
     */
    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<Resource> streamEpisode(
            @PathVariable Long episodeId,
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        
        log.info("Received request to stream episode: episodeId={}, userId={}, range={}", episodeId, userId, rangeHeader);
        
        return videoStreamingService.streamEpisode(episodeId, userId, rangeHeader);
    }
}






