package com.social.horror_pool.service;


import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.payload.GenreAllResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;

import java.util.List;

public interface GenreService {
    GenreDTO addGenre(@Valid GenreDTO genreDTO);

    GenreAllResponse getAllGenres(Integer pageNumber, Integer pageSize, String order);

    GenreDTO editGenre(@Valid GenreDTO genreDTO, Long genreId);

    GenreDTO deleteGenre(Long genreId);
}
