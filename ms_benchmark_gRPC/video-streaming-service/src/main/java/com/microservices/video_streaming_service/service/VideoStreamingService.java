package com.microservices.video_streaming_service.service;

import com.microservices.video_streaming_service.grpc.ContentServiceGrpcClient;
import com.microservices.video_streaming_service.grpc.SubscriptionServiceGrpcClient;
import com.microservices.video_streaming_service.dto.response.ContentResponse;
import com.microservices.video_streaming_service.dto.response.SubscriptionResponse;
import com.microservices.video_streaming_service.exception.BadRequestException;
import com.microservices.video_streaming_service.exception.ResourceNotFoundException;
import com.microservices.video_streaming_service.exception.SubscriptionRequiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Video Streaming Service - Video Streaming Service
 * Video dosyalarını stream etme işlemlerini yönetir
 */
@Service
public class VideoStreamingService {

    private static final Logger log = LoggerFactory.getLogger(VideoStreamingService.class);

    private final ContentServiceGrpcClient contentServiceGrpcClient;
    private final SubscriptionServiceGrpcClient subscriptionServiceGrpcClient;

    @Value("${video.base-path:${user.home}/videos}")
    private String videoBasePath;

    public VideoStreamingService(ContentServiceGrpcClient contentServiceGrpcClient,
            SubscriptionServiceGrpcClient subscriptionServiceGrpcClient) {
        this.contentServiceGrpcClient = contentServiceGrpcClient;
        this.subscriptionServiceGrpcClient = subscriptionServiceGrpcClient;
    }

    /**
     * Content ID'ye göre videoyu stream et
     * HTTP Range Request desteği ile (206 Partial Content)
     * Önce abonelik kontrolü yapılır
     */
    public ResponseEntity<Resource> streamContent(Long contentId, String userId, String rangeHeader) {
        log.info("Streaming content for contentId: {}, userId: {}, range: {}", contentId, userId, rangeHeader);

        // Abonelik kontrolü - sadece ilk istekte (Range header yoksa)
        // Sonraki chunk isteklerinde (Range: bytes=...) kontrol yapılmaz (performans
        // optimizasyonu)
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            verifySubscription(userId);
        }

        // Content Management Service'ten video dosya yolunu al
        ContentResponse content;
        try {
            content = contentServiceGrpcClient.getContentById(contentId);
        } catch (Exception e) {
            log.error("Failed to fetch content from Content Management Service: {}", e.getMessage());
            throw new ResourceNotFoundException("Content not found for content ID: " + contentId);
        }

        if (content == null || content.getVideoFilePath() == null) {
            throw new ResourceNotFoundException("Content or video file path not found for content ID: " + contentId);
        }

        // Video dosyasını bul
        String videoFilePath = content.getVideoFilePath();
        Path videoPath = Paths.get(videoFilePath);

        // Eğer relative path ise base path'i ekle
        if (!videoPath.isAbsolute()) {
            videoPath = Paths.get(videoBasePath, videoFilePath);
        }

        File videoFile = videoPath.toFile();

        if (!videoFile.exists() || !videoFile.isFile()) {
            log.error("Video file not found: {}", videoPath);
            throw new ResourceNotFoundException("Video file not found: " + videoFilePath);
        }

        if (!videoFile.canRead()) {
            log.error("Video file is not readable: {}", videoPath);
            throw new BadRequestException("Video file is not accessible");
        }

        long fileSize = videoFile.length();
        long start = 0;
        long end = fileSize - 1;
        long contentLength = fileSize;

        // Range header kontrolü
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                if (ranges.length > 0 && !ranges[0].isEmpty()) {
                    start = Long.parseLong(ranges[0]);
                }
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid range header: {}", rangeHeader);
                throw new BadRequestException("Invalid range header format");
            }

            // Range validation
            if (start < 0 || start >= fileSize || end >= fileSize || start > end) {
                throw new BadRequestException("Range not satisfiable");
            }

            contentLength = end - start + 1;
        }

        // Content type belirleme
        String contentType = determineContentType(videoFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

        // Range request ise 206 Partial Content, değilse 200 OK
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileSize));
            headers.setContentLength(contentLength);

            log.info("Streaming partial content: bytes {}-{}/{}", start, end, fileSize);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new RangeAwareResource(videoFile, start, end));
        } else {
            log.info("Streaming full content: size={}", fileSize);

            Resource resource = new FileSystemResource(videoFile);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }
    }

    /**
     * Episode ID'ye göre videoyu stream et
     * HTTP Range Request desteği ile (206 Partial Content)
     * Önce abonelik kontrolü yapılır
     */
    public ResponseEntity<Resource> streamEpisode(Long episodeId, String userId, String rangeHeader) {
        log.info("Streaming episode for episodeId: {}, userId: {}, range: {}", episodeId, userId, rangeHeader);

        // Abonelik kontrolü - sadece ilk istekte (Range header yoksa)
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            verifySubscription(userId);
        }

        // Content Management Service'ten episode bilgilerini al
        // Not: Episode için özel endpoint yoksa, episode ID'yi content ID gibi
        // kullanabiliriz
        // veya Content Management Service'e episode endpoint'i eklenebilir
        // Şimdilik episode ID'yi direkt videoFilePath olarak kullanmayacağız
        // Content Management Service'e episode endpoint'i eklenene kadar bu method'u
        // kullanmayabiliriz

        throw new BadRequestException("Episode streaming not yet implemented. Please use content streaming.");
    }

    /**
     * Kullanıcının aktif aboneliğini kontrol et
     * Abonelik yoksa SubscriptionRequiredException fırlat
     */
    private void verifySubscription(String userId) {
        try {
            SubscriptionResponse subscription = subscriptionServiceGrpcClient.getActiveSubscription(userId);

            if (subscription == null || !"ACTIVE".equals(subscription.getStatus())) {
                log.warn("User {} does not have an active subscription", userId);
                throw new SubscriptionRequiredException("Active subscription required to stream content");
            }

            log.debug("User {} has active subscription: {}", userId, subscription.getId());
        } catch (SubscriptionRequiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify subscription for user {}: {}", userId, e.getMessage());
            throw new SubscriptionRequiredException(
                    "Active subscription required to stream content. Unable to verify subscription status.");
        }
    }

    /**
     * Dosya uzantısına göre content type belirle
     */
    private String determineContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        if (lowerPath.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith(".webm")) {
            return "video/webm";
        } else if (lowerPath.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (lowerPath.endsWith(".avi")) {
            return "video/x-msvideo";
        } else {
            // Default olarak mp4
            return "video/mp4";
        }
    }

    /**
     * Range-aware Resource wrapper
     * Sadece belirtilen byte range'ini okur
     */
    private static class RangeAwareResource extends FileSystemResource {
        private final long start;
        private final long end;

        public RangeAwareResource(File file, long start, long end) {
            super(file);
            this.start = start;
            this.end = end;
        }

        @Override
        public long contentLength() throws IOException {
            return end - start + 1;
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
            randomAccessFile.seek(start);

            return new java.io.InputStream() {
                private long bytesRead = 0;
                private final long maxBytes = end - start + 1;

                @Override
                public int read() throws IOException {
                    if (bytesRead >= maxBytes) {
                        close();
                        return -1;
                    }
                    bytesRead++;
                    return randomAccessFile.read();
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if (bytesRead >= maxBytes) {
                        close();
                        return -1;
                    }
                    long remaining = maxBytes - bytesRead;
                    int toRead = (int) Math.min(len, remaining);
                    int read = randomAccessFile.read(b, off, toRead);
                    if (read > 0) {
                        bytesRead += read;
                    }
                    if (bytesRead >= maxBytes) {
                        close();
                    }
                    return read;
                }

                @Override
                public void close() throws IOException {
                    randomAccessFile.close();
                }

                @Override
                public int available() throws IOException {
                    return (int) Math.min(maxBytes - bytesRead, Integer.MAX_VALUE);
                }
            };
        }
    }
}
