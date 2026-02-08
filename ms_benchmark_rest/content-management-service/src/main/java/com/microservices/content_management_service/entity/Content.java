package com.microservices.content_management_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Content Entity - Content Management Service
 * Film ve dizileri temsil eder
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"contentGenres", "contentCasts", "seasons"})
@ToString(exclude = {"contentGenres", "contentCasts", "seasons"})
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ContentType contentType; // MOVIE, TV_SERIES

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // Film için toplam süre, dizi için bölüm başına ortalama süre

    @Column(name = "video_file_path", nullable = false, columnDefinition = "TEXT")
    private String videoFilePath; // Local dosya yolu

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "trailer_url", columnDefinition = "TEXT")
    private String trailerUrl;

    @Column(name = "rating", columnDefinition = "NUMERIC(3,1)")
    private Double rating; // Ortalama rating (1.0 - 10.0)

    @Column(name = "age_rating", length = 10)
    private String ageRating; // ALL, PG, PG13, R, NC17

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "tr"; // tr, en, fr, de, etc.

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT; // DRAFT, PUBLISHED, COMING_SOON, ARCHIVED

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false; // Öne çıkan içerik mi?

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "total_seasons")
    private Integer totalSeasons; // Sadece TV_SERIES için

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete

    // Relationships
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ContentGenre> contentGenres = new HashSet<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ContentCast> contentCasts = new HashSet<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Season> seasons = new HashSet<>();

    public enum ContentType {
        MOVIE,
        TV_SERIES
    }

    public enum ContentStatus {
        DRAFT,
        PUBLISHED,
        COMING_SOON,
        ARCHIVED
    }
}

