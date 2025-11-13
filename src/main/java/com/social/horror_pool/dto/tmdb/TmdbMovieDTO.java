package com.social.horror_pool.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieDTO {

    private Long id; // tmdb id
    private String title;
    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    private Boolean adult;
    private Boolean video;
    private Double popularity;
}
