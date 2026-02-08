package com.microservices.content_management_service.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ContentCast Entity - Content Management Service
 * Content ve CastCrew arasındaki many-to-many ilişkiyi temsil eder
 */
@Entity
@Table(name = "content_cast", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"content_id", "cast_crew_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"content", "castCrew"})
@ToString(exclude = {"content", "castCrew"})
public class ContentCast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cast_crew_id", nullable = false)
    private CastCrew castCrew;

    @Column(name = "character_name", length = 255)
    private String characterName; // Oyuncunun canlandırdığı karakter adı

    @Column(name = "role_type", length = 20)
    @Enumerated(EnumType.STRING)
    private CastCrew.RoleType roleType; // ACTOR, DIRECTOR, WRITER, PRODUCER
}

