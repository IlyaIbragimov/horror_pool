package com.social.horror_pool.controller;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.payload.tmdb.BulkImportResultResponse;
import com.social.horror_pool.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TMDB", description = "Endpoints for movie import from tmdb")
@RestController
@RequestMapping("/horrorpool/admin/tmdb")
public class TmdbController {

    private final MovieService movieService;

    public TmdbController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(
            summary = "Import one movie from TMDB by tmdbId",
            description = "Import one movie from TMDB by tmdbId to the database. Available only for administrator"
    )
    @PostMapping("/import/{tmdbId}")
    public ResponseEntity<MovieDTO> importMovieFromTmdb(
            @PathVariable Long tmdbId,
            @RequestParam(defaultValue = "en-US") String language) {
        MovieDTO result = this.movieService.importFromTmdb(tmdbId, language);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Bulk import from TMDB",
            description = "Import given amount movies from TMDB (20 movies per one page). Available only for administrator"
    )
    @PostMapping("/bulkImport")
    public ResponseEntity<BulkImportResultResponse> bulkImportFromTmd(
            @RequestParam(defaultValue = "1") Integer pages,
            @RequestParam(defaultValue = "en-US") String language) {
        BulkImportResultResponse result = this.movieService.bulkImportFromTmdb(pages, language);
        return new ResponseEntity<BulkImportResultResponse>(result, HttpStatus.OK);
    }
}