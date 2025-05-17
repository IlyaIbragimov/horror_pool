package com.social.horror_pool.controller;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horrorpool")
public class GenreController {

    final private GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/admin/genre/add")
    public ResponseEntity<GenreDTO> addGenre(@Valid @RequestBody GenreDTO genreDTO) {
        GenreDTO result = this.genreService.addGenre(genreDTO);
        return new ResponseEntity<GenreDTO>(result, HttpStatus.CREATED);
    }

    @GetMapping("/genre/all")
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> result = this.genreService.getAllGenres();
        return new ResponseEntity<List<GenreDTO>>(result, HttpStatus.OK);
    }

}
