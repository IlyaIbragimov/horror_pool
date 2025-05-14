package com.social.horror_pool.service;


import com.social.horror_pool.dto.GenreDTO;
import jakarta.validation.Valid;

public interface GenreService {
    GenreDTO addGenre(@Valid GenreDTO genreDTO);
}
