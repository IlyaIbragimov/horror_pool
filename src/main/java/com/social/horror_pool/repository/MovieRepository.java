package com.social.horror_pool.repository;

import com.social.horror_pool.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    boolean existsByTitleIgnoreCaseAndReleaseDate(String title, LocalDate releaseDate);
    boolean existsByTitleIgnoreCaseAndReleaseDateAndMovieIdNot(String title, LocalDate releaseDate, Long movieId);
    Optional<Movie> findByTmdbId(Long tmdbId);
    boolean existsByTmdbId(Long tmdbId);
    Page<Movie> findByGenres_GenreId(Long genreId, Pageable pageable);
}