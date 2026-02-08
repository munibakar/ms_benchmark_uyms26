package com.microservices.content_management_service.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ContentGenre Entity - Content Management Service
 * Content ve Genre arasındaki many-to-many ilişkiyi temsil eder
 */
@Entity
@Table(name = "content_genres", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"content_id", "genre_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"content", "genre"})
@ToString(exclude = {"content", "genre"})
public class ContentGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}

