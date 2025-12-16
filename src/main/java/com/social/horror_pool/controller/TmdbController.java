package com.social.horror_pool.controller;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.service.MovieService;
import org.springframework.http.HttpStatus;
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
        MovieDTO result = this.movieService.importFromTmdb(tmdbId, language);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }
}