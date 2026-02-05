package com.social.horror_pool.service;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.payload.GenreAllResponse;
import jakarta.validation.Valid;

public interface GenreService {
    GenreDTO addGenre(@Valid GenreDTO genreDTO);

    GenreAllResponse getAllGenres(Integer pageNumber, Integer pageSize, String order);

    GenreDTO editGenre(@Valid GenreDTO genreDTO, Long genreId);

    GenreDTO deleteGenre(Long genreId);

    GenreAllResponse getGenresByKeyword(Integer pageNumber, Integer pageSize, String order, String keyword);
}
