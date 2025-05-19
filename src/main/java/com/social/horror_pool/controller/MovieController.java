package com.social.horror_pool.controller;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horrorpool")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/admin/movie/add")
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDTO){
        MovieDTO result = this.movieService.addMovie(movieDTO);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.CREATED);
    }

    @GetMapping("/movie/all")
    public ResponseEntity<List<MovieDTO>> getAllMovies(){
        List<MovieDTO> result = this.movieService.getAllMovies();
        return new ResponseEntity<List<MovieDTO>>(result, HttpStatus.OK);
    }
}
