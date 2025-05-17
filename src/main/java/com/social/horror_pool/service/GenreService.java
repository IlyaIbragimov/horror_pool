package com.social.horror_pool.service;


import com.social.horror_pool.dto.GenreDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface GenreService {
    GenreDTO addGenre(@Valid GenreDTO genreDTO);

    List<GenreDTO> getAllGenres();

    GenreDTO editGenre(@Valid GenreDTO genreDTO, Long genreId);

    GenreDTO deleteGenre(Long genreId);
}
