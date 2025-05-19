package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import jakarta.validation.Valid;

public interface MovieService {
    MovieDTO addMovie(@Valid MovieDTO movieDTO);
}
