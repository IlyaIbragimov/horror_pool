package com.social.horror_pool.dto;

import com.social.horror_pool.model.Genre;
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

    private List<Genre> genres = new ArrayList<>();
}
