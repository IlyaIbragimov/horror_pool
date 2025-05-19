package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface MovieService {
    MovieDTO addMovie(@Valid MovieDTO movieDTO);

    List<MovieDTO> getAllMovies();

    MovieDTO editMovie(@Valid MovieDTO movieDTO, Long movieId);
}
