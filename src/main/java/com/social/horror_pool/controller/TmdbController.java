package com.social.horror_pool.controller;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/horrorpool/admin/tmdb")
public class TmdbController {

    private final MovieService movieService;

    public TmdbController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/import/{tmdbId}")
    public ResponseEntity<MovieDTO> importMovie(
            @PathVariable Long tmdbId,
            @RequestParam(defaultValue = "en-US") String language) {
        return this.movieService.importFromTmdb(tmdbId, language)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}