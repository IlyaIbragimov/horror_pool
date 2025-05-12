package com.social.horror_pool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Movie {

    @Id
    @EqualsAndHashCode.Include
    private Long movieId;

    private String title;
    private String originalTitle;
    private String description;
    private String overview;
    private LocalDate releaseDate;

    private String posterPath;
    private String backdropPath;

    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;

    private String originalLanguage;
    private Boolean adult;
    private Boolean video;

    @ManyToMany
    private Set<Genre> genres;

}
