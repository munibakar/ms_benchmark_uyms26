package com.microservices.content_management_service.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.microservices.content_management_service.entity.CastCrew;
import com.microservices.content_management_service.entity.CastCrew.RoleType;
import com.microservices.content_management_service.entity.Content;
import com.microservices.content_management_service.entity.Content.ContentStatus;
import com.microservices.content_management_service.entity.Content.ContentType;
import com.microservices.content_management_service.entity.ContentCast;
import com.microservices.content_management_service.entity.ContentGenre;
import com.microservices.content_management_service.entity.Episode;
import com.microservices.content_management_service.entity.Genre;
import com.microservices.content_management_service.entity.Season;
import com.microservices.content_management_service.repository.CastCrewRepository;
import com.microservices.content_management_service.repository.ContentCastRepository;
import com.microservices.content_management_service.repository.ContentGenreRepository;
import com.microservices.content_management_service.repository.ContentRepository;
import com.microservices.content_management_service.repository.EpisodeRepository;
import com.microservices.content_management_service.repository.GenreRepository;
import com.microservices.content_management_service.repository.SeasonRepository;

/**
 * Data Initializer
 * Uygulama başlatıldığında gerekli başlangıç verilerini yükler
 * 4 adet örnek video (film ve dizi) otomatik olarak eklenir
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final GenreRepository genreRepository;
    private final CastCrewRepository castCrewRepository;
    private final ContentRepository contentRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final ContentCastRepository contentCastRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;

    public DataInitializer(
            GenreRepository genreRepository,
            CastCrewRepository castCrewRepository,
            ContentRepository contentRepository,
            ContentGenreRepository contentGenreRepository,
            ContentCastRepository contentCastRepository,
            SeasonRepository seasonRepository,
            EpisodeRepository episodeRepository) {
        this.genreRepository = genreRepository;
        this.castCrewRepository = castCrewRepository;
        this.contentRepository = contentRepository;
        this.contentGenreRepository = contentGenreRepository;
        this.contentCastRepository = contentCastRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Eğer zaten içerik varsa, tekrar ekleme
        if (contentRepository.count() > 0) {
            log.info("Content already exists. Skipping initialization.");
            return;
        }

        initializeGenres();
        initializeCastCrew();
        initializeContents();
        
        log.info("Data initialization completed successfully!");
    }

    /**
     * Genre (Tür) verilerini initialize et
     */
    private void initializeGenres() {
        log.info("Initializing genres...");

        Genre action = Genre.builder()
                .name("Aksiyon")
                .description("Yüksek tempolu aksiyon sahneleri içeren filmler")
                .isActive(true)
                .build();

        Genre sciFi = Genre.builder()
                .name("Bilim Kurgu")
                .description("Bilim ve teknoloji temalı kurgu eserleri")
                .isActive(true)
                .build();

        Genre drama = Genre.builder()
                .name("Drama")
                .description("Duygusal ve ciddi konuları işleyen eserler")
                .isActive(true)
                .build();

        Genre thriller = Genre.builder()
                .name("Gerilim")
                .description("Gerilim ve heyecan dolu eserler")
                .isActive(true)
                .build();

        Genre crime = Genre.builder()
                .name("Suç")
                .description("Suç ve adalet temalı eserler")
                .isActive(true)
                .build();

        Genre horror = Genre.builder()
                .name("Korku")
                .description("Korku ve gerilim temalı eserler")
                .isActive(true)
                .build();

        genreRepository.save(action);
        genreRepository.save(sciFi);
        genreRepository.save(drama);
        genreRepository.save(thriller);
        genreRepository.save(crime);
        genreRepository.save(horror);

        log.info("Successfully initialized 6 genres");
    }

    /**
     * Cast/Crew verilerini initialize et
     */
    private void initializeCastCrew() {
        log.info("Initializing cast/crew...");

        // Actors
        CastCrew keanuReeves = CastCrew.builder()
                .name("Keanu Reeves")
                .biography("Kanadalı aktör ve yapımcı")
                .roleType(RoleType.ACTOR)
                .nationality("Kanada")
                .dateOfBirth(LocalDateTime.of(1964, 9, 2, 0, 0))
                .isActive(true)
                .build();

        CastCrew leonardoDiCaprio = CastCrew.builder()
                .name("Leonardo DiCaprio")
                .biography("Amerikalı aktör ve yapımcı")
                .roleType(RoleType.ACTOR)
                .nationality("ABD")
                .dateOfBirth(LocalDateTime.of(1974, 11, 11, 0, 0))
                .isActive(true)
                .build();

        CastCrew bryanCranston = CastCrew.builder()
                .name("Bryan Cranston")
                .biography("Amerikalı aktör, yönetmen ve senarist")
                .roleType(RoleType.ACTOR)
                .nationality("ABD")
                .dateOfBirth(LocalDateTime.of(1956, 3, 7, 0, 0))
                .isActive(true)
                .build();

        CastCrew millieBobbyBrown = CastCrew.builder()
                .name("Millie Bobby Brown")
                .biography("İngiliz aktris")
                .roleType(RoleType.ACTOR)
                .nationality("İngiltere")
                .dateOfBirth(LocalDateTime.of(2004, 2, 19, 0, 0))
                .isActive(true)
                .build();

        // Directors
        CastCrew wachowskiSisters = CastCrew.builder()
                .name("Lana & Lilly Wachowski")
                .biography("Amerikalı yönetmen ve senarist kardeşler")
                .roleType(RoleType.DIRECTOR)
                .nationality("ABD")
                .isActive(true)
                .build();

        CastCrew christopherNolan = CastCrew.builder()
                .name("Christopher Nolan")
                .biography("İngiliz-Amerikalı yönetmen ve senarist")
                .roleType(RoleType.DIRECTOR)
                .nationality("İngiltere")
                .dateOfBirth(LocalDateTime.of(1970, 7, 30, 0, 0))
                .isActive(true)
                .build();

        CastCrew vinceGilligan = CastCrew.builder()
                .name("Vince Gilligan")
                .biography("Amerikalı yönetmen ve senarist")
                .roleType(RoleType.DIRECTOR)
                .nationality("ABD")
                .isActive(true)
                .build();

        CastCrew dufferBrothers = CastCrew.builder()
                .name("Duffer Brothers")
                .biography("Amerikalı yönetmen ve senarist kardeşler")
                .roleType(RoleType.DIRECTOR)
                .nationality("ABD")
                .isActive(true)
                .build();

        castCrewRepository.save(keanuReeves);
        castCrewRepository.save(leonardoDiCaprio);
        castCrewRepository.save(bryanCranston);
        castCrewRepository.save(millieBobbyBrown);
        castCrewRepository.save(wachowskiSisters);
        castCrewRepository.save(christopherNolan);
        castCrewRepository.save(vinceGilligan);
        castCrewRepository.save(dufferBrothers);

        log.info("Successfully initialized 8 cast/crew members");
    }

    /**
     * Content (İçerik) verilerini initialize et
     */
    private void initializeContents() {
        log.info("Initializing contents...");

        // Genre'leri al
        Genre action = genreRepository.findByNameIgnoreCase("Aksiyon").orElse(null);
        Genre sciFi = genreRepository.findByNameIgnoreCase("Bilim Kurgu").orElse(null);
        Genre drama = genreRepository.findByNameIgnoreCase("Drama").orElse(null);
        Genre thriller = genreRepository.findByNameIgnoreCase("Gerilim").orElse(null);
        Genre crime = genreRepository.findByNameIgnoreCase("Suç").orElse(null);
        Genre horror = genreRepository.findByNameIgnoreCase("Korku").orElse(null);

        // Cast/Crew'leri al
        CastCrew keanuReeves = castCrewRepository.searchByName("Keanu Reeves").stream().findFirst().orElse(null);
        CastCrew wachowskiSisters = castCrewRepository.searchByName("Lana & Lilly Wachowski").stream().findFirst().orElse(null);
        CastCrew leonardoDiCaprio = castCrewRepository.searchByName("Leonardo DiCaprio").stream().findFirst().orElse(null);
        CastCrew christopherNolan = castCrewRepository.searchByName("Christopher Nolan").stream().findFirst().orElse(null);
        CastCrew bryanCranston = castCrewRepository.searchByName("Bryan Cranston").stream().findFirst().orElse(null);
        CastCrew vinceGilligan = castCrewRepository.searchByName("Vince Gilligan").stream().findFirst().orElse(null);
        CastCrew millieBobbyBrown = castCrewRepository.searchByName("Millie Bobby Brown").stream().findFirst().orElse(null);
        CastCrew dufferBrothers = castCrewRepository.searchByName("Duffer Brothers").stream().findFirst().orElse(null);

        // 1. The Matrix (Film)
        Content theMatrix = Content.builder()
                .title("The Matrix")
                .description("Bir bilgisayar korsanı olan Neo, gerçekliğin gerçekte ne olduğunu keşfeder. Matrix adı verilen simüle edilmiş bir gerçeklikte yaşadığını öğrenir ve makinelerin insanlığı kontrol ettiğini anlar.")
                .contentType(ContentType.MOVIE)
                .releaseYear(1999)
                .durationMinutes(136)
                .videoFilePath("/videos/movies/the-matrix-1999.mp4")
                .posterUrl("https://example.com/posters/the-matrix.jpg")
                .thumbnailUrl("https://example.com/thumbnails/the-matrix.jpg")
                .trailerUrl("https://example.com/trailers/the-matrix.mp4")
                .rating(8.7)
                .ageRating("R")
                .language("en")
                .status(ContentStatus.PUBLISHED)
                .isFeatured(true)
                .viewCount(0L)
                .isActive(true)
                .build();

        theMatrix = contentRepository.save(theMatrix);

        // The Matrix - Genre ilişkileri
        if (action != null && sciFi != null && thriller != null) {
            contentGenreRepository.save(ContentGenre.builder().content(theMatrix).genre(action).build());
            contentGenreRepository.save(ContentGenre.builder().content(theMatrix).genre(sciFi).build());
            contentGenreRepository.save(ContentGenre.builder().content(theMatrix).genre(thriller).build());
        }

        // The Matrix - Cast ilişkileri
        if (keanuReeves != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(theMatrix)
                    .castCrew(keanuReeves)
                    .characterName("Neo")
                    .roleType(RoleType.ACTOR)
                    .build());
        }
        if (wachowskiSisters != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(theMatrix)
                    .castCrew(wachowskiSisters)
                    .roleType(RoleType.DIRECTOR)
                    .build());
        }

        // 2. Inception (Film)
        Content inception = Content.builder()
                .title("Inception")
                .description("Dom Cobb, bir hırsız ve aynı zamanda rüyalara girebilen nadir bir yeteneğe sahip biridir. Son bir işi kabul ederek, imkansız bir görevi yerine getirmeye çalışır: fikir hırsızlığı yerine bir fikir yerleştirme.")
                .contentType(ContentType.MOVIE)
                .releaseYear(2010)
                .durationMinutes(148)
                .videoFilePath("/videos/movies/inception-2010.mp4")
                .posterUrl("https://example.com/posters/inception.jpg")
                .thumbnailUrl("https://example.com/thumbnails/inception.jpg")
                .trailerUrl("https://example.com/trailers/inception.mp4")
                .rating(8.8)
                .ageRating("PG13")
                .language("en")
                .status(ContentStatus.PUBLISHED)
                .isFeatured(true)
                .viewCount(0L)
                .isActive(true)
                .build();

        inception = contentRepository.save(inception);

        // Inception - Genre ilişkileri
        if (sciFi != null && action != null && thriller != null) {
            contentGenreRepository.save(ContentGenre.builder().content(inception).genre(sciFi).build());
            contentGenreRepository.save(ContentGenre.builder().content(inception).genre(action).build());
            contentGenreRepository.save(ContentGenre.builder().content(inception).genre(thriller).build());
        }

        // Inception - Cast ilişkileri
        if (leonardoDiCaprio != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(inception)
                    .castCrew(leonardoDiCaprio)
                    .characterName("Dom Cobb")
                    .roleType(RoleType.ACTOR)
                    .build());
        }
        if (christopherNolan != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(inception)
                    .castCrew(christopherNolan)
                    .roleType(RoleType.DIRECTOR)
                    .build());
        }

        // 3. Breaking Bad (TV Series)
        Content breakingBad = Content.builder()
                .title("Breaking Bad")
                .description("Lise kimya öğretmeni Walter White, akciğer kanseri olduğunu öğrenir. Ailesinin geleceğini güvence altına almak için uyuşturucu üretmeye başlar.")
                .contentType(ContentType.TV_SERIES)
                .releaseYear(2008)
                .durationMinutes(45)
                .videoFilePath("/videos/series/breaking-bad/season1/episode1.mp4")
                .posterUrl("https://example.com/posters/breaking-bad.jpg")
                .thumbnailUrl("https://example.com/thumbnails/breaking-bad.jpg")
                .trailerUrl("https://example.com/trailers/breaking-bad.mp4")
                .rating(9.5)
                .ageRating("R")
                .language("en")
                .status(ContentStatus.PUBLISHED)
                .isFeatured(true)
                .totalSeasons(5)
                .viewCount(0L)
                .isActive(true)
                .build();

        breakingBad = contentRepository.save(breakingBad);

        // Breaking Bad - Genre ilişkileri
        if (drama != null && crime != null && thriller != null) {
            contentGenreRepository.save(ContentGenre.builder().content(breakingBad).genre(drama).build());
            contentGenreRepository.save(ContentGenre.builder().content(breakingBad).genre(crime).build());
            contentGenreRepository.save(ContentGenre.builder().content(breakingBad).genre(thriller).build());
        }

        // Breaking Bad - Cast ilişkileri
        if (bryanCranston != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(breakingBad)
                    .castCrew(bryanCranston)
                    .characterName("Walter White")
                    .roleType(RoleType.ACTOR)
                    .build());
        }
        if (vinceGilligan != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(breakingBad)
                    .castCrew(vinceGilligan)
                    .roleType(RoleType.DIRECTOR)
                    .build());
        }

        // Breaking Bad - Season 1
        Season breakingBadSeason1 = Season.builder()
                .content(breakingBad)
                .seasonNumber(1)
                .title("Season 1")
                .description("Walter White'in uyuşturucu işine başladığı ilk sezon")
                .releaseYear(2008)
                .posterUrl("https://example.com/posters/breaking-bad-s1.jpg")
                .isActive(true)
                .build();

        breakingBadSeason1 = seasonRepository.save(breakingBadSeason1);

        // Breaking Bad - Episode 1
        Episode breakingBadE1 = Episode.builder()
                .season(breakingBadSeason1)
                .episodeNumber(1)
                .title("Pilot")
                .description("Walter White, kanser olduğunu öğrenir ve uyuşturucu üretmeye başlar.")
                .durationMinutes(58)
                .videoFilePath("/videos/series/breaking-bad/season1/episode1.mp4")
                .thumbnailUrl("https://example.com/thumbnails/breaking-bad-s1-e1.jpg")
                .releaseDate(LocalDateTime.of(2008, 1, 20, 0, 0))
                .isActive(true)
                .build();

        episodeRepository.save(breakingBadE1);

        // 4. Stranger Things (TV Series)
        Content strangerThings = Content.builder()
                .title("Stranger Things")
                .description("1980'lerde geçen, küçük bir kasabada kaybolan bir çocuğu arayan arkadaşların ve ailesinin hikayesi. Sıradışı gizemler ve süper güçler ortaya çıkar.")
                .contentType(ContentType.TV_SERIES)
                .releaseYear(2016)
                .durationMinutes(50)
                .videoFilePath("/videos/series/stranger-things/season1/episode1.mp4")
                .posterUrl("https://example.com/posters/stranger-things.jpg")
                .thumbnailUrl("https://example.com/thumbnails/stranger-things.jpg")
                .trailerUrl("https://example.com/trailers/stranger-things.mp4")
                .rating(8.7)
                .ageRating("PG13")
                .language("en")
                .status(ContentStatus.PUBLISHED)
                .isFeatured(true)
                .totalSeasons(4)
                .viewCount(0L)
                .isActive(true)
                .build();

        strangerThings = contentRepository.save(strangerThings);

        // Stranger Things - Genre ilişkileri
        if (sciFi != null && drama != null && horror != null) {
            contentGenreRepository.save(ContentGenre.builder().content(strangerThings).genre(sciFi).build());
            contentGenreRepository.save(ContentGenre.builder().content(strangerThings).genre(drama).build());
            contentGenreRepository.save(ContentGenre.builder().content(strangerThings).genre(horror).build());
        }

        // Stranger Things - Cast ilişkileri
        if (millieBobbyBrown != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(strangerThings)
                    .castCrew(millieBobbyBrown)
                    .characterName("Eleven")
                    .roleType(RoleType.ACTOR)
                    .build());
        }
        if (dufferBrothers != null) {
            contentCastRepository.save(ContentCast.builder()
                    .content(strangerThings)
                    .castCrew(dufferBrothers)
                    .roleType(RoleType.DIRECTOR)
                    .build());
        }

        // Stranger Things - Season 1
        Season strangerThingsSeason1 = Season.builder()
                .content(strangerThings)
                .seasonNumber(1)
                .title("Season 1")
                .description("Will'in kaybolması ve Eleven'in ortaya çıkışı")
                .releaseYear(2016)
                .posterUrl("https://example.com/posters/stranger-things-s1.jpg")
                .isActive(true)
                .build();

        strangerThingsSeason1 = seasonRepository.save(strangerThingsSeason1);

        // Stranger Things - Episode 1
        Episode strangerThingsE1 = Episode.builder()
                .season(strangerThingsSeason1)
                .episodeNumber(1)
                .title("Chapter One: The Vanishing of Will Byers")
                .description("Will Byers kaybolur ve arkadaşları onu aramaya başlar.")
                .durationMinutes(47)
                .videoFilePath("/videos/series/stranger-things/season1/episode1.mp4")
                .thumbnailUrl("https://example.com/thumbnails/stranger-things-s1-e1.jpg")
                .releaseDate(LocalDateTime.of(2016, 7, 15, 0, 0))
                .isActive(true)
                .build();

        episodeRepository.save(strangerThingsE1);

        log.info("Successfully initialized 4 contents:");
        log.info("  1. The Matrix (Movie)");
        log.info("  2. Inception (Movie)");
        log.info("  3. Breaking Bad (TV Series)");
        log.info("  4. Stranger Things (TV Series)");
    }
}

