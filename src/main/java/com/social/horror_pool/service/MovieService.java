package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.payload.tmdb.BulkImportResultResponse;
import jakarta.validation.Valid;

public interface MovieService {
    MovieDTO addMovie(@Valid MovieDTO movieDTO);
    MovieAllResponse getAllMovies(Integer pageNumber, Integer pageSize, String sortBy, String order);
    MovieDTO editMovie(@Valid MovieDTO movieDTO, Long movieId);
    MovieDTO deleteMovie(Long movieId);
    MovieAllResponse getMoviesByKeyword(Integer pageNumber, Integer pageSize, String sort, String order, String keyword, Integer year, String language, Boolean adult, Double voteAverage, Double popularity);
    MovieDTO getMovieById(Long movieId);
    MovieDTO getMovieByTmdbId(Long tmdbId);
    boolean checkIftmdbIdExists(Long tmdbId);
    MovieDTO importFromTmdb(Long tmdbId, String language);
    BulkImportResultResponse bulkImportFromTmdb(Integer pages, String language);
}
