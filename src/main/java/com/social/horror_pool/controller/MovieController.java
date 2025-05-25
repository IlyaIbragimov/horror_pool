package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<MovieAllResponse> getAllMovies(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam (name = "sort", defaultValue = AppConstants.SORT_TYPE_MOVIE_DEFAULT, required = false) String sort,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        MovieAllResponse result = this.movieService.getAllMovies(pageNumber, pageSize, sort, order);
        return new ResponseEntity<MovieAllResponse>(result, HttpStatus.OK);
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieAllResponse> searchMoviesByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam (name = "sort", defaultValue = AppConstants.SORT_TYPE_MOVIE_DEFAULT, required = false) String sort,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ) {
        MovieAllResponse result = this.movieService.getMoviesByKeyword(pageNumber, pageSize, sort, order, keyword);
        return new ResponseEntity<MovieAllResponse>(result, HttpStatus.OK);
    }

    @PutMapping("/admin/movie/{movieId}/edit")
    public ResponseEntity<MovieDTO> editMovie(@Valid @RequestBody MovieDTO movieDTO, @PathVariable Long movieId){
        MovieDTO result = this.movieService.editMovie(movieDTO,movieId);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }

    @DeleteMapping("/admin/movie/{movieId}/delete")
    public ResponseEntity<MovieDTO> deleteMovie(@PathVariable Long movieId){
        MovieDTO result = this.movieService.deleteMovie(movieId);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }




}
