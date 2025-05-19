package com.social.horror_pool.repository;

import com.social.horror_pool.model.Movie;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Movie findByTitle(@NotBlank(message = "Movie title cannot be empty") String title);
}
