package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.payload.GenreAllResponse;
import com.social.horror_pool.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Genre", description = "Endpoints for genre creation, update, listing and search")
@RestController
@RequestMapping("/horrorpool")
public class GenreController {

    final private GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @Operation(
            summary = "Add a new genre",
            description = "Create a new genre. Only accessible to administrators."
    )
    @PostMapping("/admin/genre/add")
    public ResponseEntity<GenreDTO> addGenre(@Valid @RequestBody GenreDTO genreDTO) {
        GenreDTO result = this.genreService.addGenre(genreDTO);
        return new ResponseEntity<GenreDTO>(result, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Edit a genre",
            description = "Update an existing genre by genre ID. Only accessible to administrators."
    )
    @PutMapping("/admin/genre/update/{genreId}")
    public ResponseEntity<GenreDTO> editGenre(@Valid @RequestBody GenreDTO genreDTO, @PathVariable Long genreId) {
        GenreDTO result = this.genreService.editGenre(genreDTO, genreId);
        return new ResponseEntity<GenreDTO>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all genres",
            description = "Retrieve a paginated list of all genres. Available for all users."
    )
    @GetMapping("/public/genre/all")
    public ResponseEntity<GenreAllResponse> getAllGenres(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ) {
        GenreAllResponse result = this.genreService.getAllGenres(pageNumber, pageSize, order);
        return new ResponseEntity<GenreAllResponse>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Search genres by keyword",
            description = "Search genres by keyword with pagination support. Available for all users."
    )
    @GetMapping("/public/genre/search")
    public ResponseEntity<GenreAllResponse> getGenresByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ) {
        GenreAllResponse result = this.genreService.getGenresByKeyword(pageNumber, pageSize, order, keyword);
        return new ResponseEntity<GenreAllResponse>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a genre",
            description = "Delete a genre by genre ID. Only accessible to administrators."
    )
    @DeleteMapping("/admin/genre/delete/{genreId}")
    public ResponseEntity<GenreDTO> deleteGenre(@PathVariable Long genreId) {
        GenreDTO result = this.genreService.deleteGenre(genreId);
        return new ResponseEntity<GenreDTO>(result, HttpStatus.OK);
    }

}
