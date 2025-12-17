package com.social.horror_pool.payload.tmdb;

import com.social.horror_pool.dto.tmdb.TmdbDiscoverMovieDTO;
import com.social.horror_pool.dto.tmdb.TmdbMovieDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbDiscoverMovieAllResponse {

    private List<TmdbDiscoverMovieDTO> movies;
    private Integer pageNumber;
    private Long totalElements;
    private Integer totalPages;
}
