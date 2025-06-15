package com.social.horror_pool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Movie title cannot be empty")
    private String title;
    private String originalTitle;
    @Size(max = 1000, message = "Description cannot be longer than 1000 characters")
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

    private List<GenreDTO> genres = new ArrayList<>();

    private List<CommentDTO> comments = new ArrayList<>();
}
