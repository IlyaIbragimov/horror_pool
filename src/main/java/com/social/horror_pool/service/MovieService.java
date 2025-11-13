package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.MovieAllResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    MovieDTO addMovie(@Valid MovieDTO movieDTO);
    MovieAllResponse getAllMovies(Integer pageNumber, Integer pageSize, String sortBy, String order);
    MovieDTO editMovie(@Valid MovieDTO movieDTO, Long movieId);
    MovieDTO deleteMovie(Long movieId);
    MovieAllResponse getMoviesByKeyword(Integer pageNumber, Integer pageSize, String sort, String order, String keyword, Integer year, String language, Boolean adult, Double voteAverage, Double popularity);
    MovieDTO getMovieById(Long movieId);
    MovieDTO getMovieByTmdbId(Long tmdbId);
    boolean checkIftmdbIdExists(Long tmdbId);
    Optional<MovieDTO> importFromTmdb(Long tmdbId, String language);
}
