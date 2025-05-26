package com.social.horror_pool.repository;

import com.social.horror_pool.model.Movie;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    Movie findByTitle(@NotBlank(message = "Movie title cannot be empty") String title);

    Page<Movie> findByTitleLikeIgnoreCase(String s, Pageable pageable);
}
