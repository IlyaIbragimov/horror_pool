package com.social.horror_pool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Long movieId;
    private Long tmdbId;
    @NotBlank(message = "Movie title cannot be empty")
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private Integer releaseYear;
    private String posterPath;

    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;
    private String trailerUrl;

    private String originalLanguage;

    private List<GenreDTO> genres;

    private List<CommentDTO> comments = new ArrayList<>();

    public MovieDTO(Long movieId, Long tmdbId, String title, String originalTitle, String overview,
                    LocalDate releaseDate, Integer releaseYear, String posterPath, Double voteAverage,
                    Integer voteCount, Double popularity, String originalLanguage,
                    List<GenreDTO> genres, List<CommentDTO> comments) {
        this(movieId, tmdbId, title, originalTitle, overview, releaseDate, releaseYear, posterPath,
                voteAverage, voteCount, popularity, null, originalLanguage, genres, comments);
    }
}
