package com.social.horror_pool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    private String title;
    private String originalTitle;
    private String description;
    private String overview;
    private LocalDate releaseDate;
    private Integer releaseYear;

    private String posterPath;
    private String backdropPath;

    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;

    private String originalLanguage;
    private Boolean adult;
    private Boolean video;

    @ManyToMany
    @JoinTable(name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", orphanRemoval = true)
    private List<WatchlistItem> watchlistItems = new ArrayList<>();

}
