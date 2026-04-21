package com.social.horror_pool.payload.tmdb;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TmdbDiscoverRequest {
    private Integer pages;
    private String sortBy;
    private LocalDate releaseDateFrom;
    private LocalDate releaseDateTo;
    private Double minVoteAverage;
}
