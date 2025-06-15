package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.service.CommentService;
import com.social.horror_pool.service.MovieService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/horrorpool")
public class MovieController {

    private final MovieService movieService;
    private final CommentService commentService;

    public MovieController(MovieService movieService, CommentService commentService) {
        this.movieService = movieService;
        this.commentService = commentService;
    }

    @PostMapping("/admin/movie/add")
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDTO){
        MovieDTO result = this.movieService.addMovie(movieDTO);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.CREATED);
    }

    @GetMapping("/public/movie/all")
    public ResponseEntity<MovieAllResponse> getAllMovies(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam (name = "sort", defaultValue = AppConstants.SORT_TYPE_MOVIE_DEFAULT, required = false) String sort,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        MovieAllResponse result = this.movieService.getAllMovies(pageNumber, pageSize, sort, order);
        return new ResponseEntity<MovieAllResponse>(result, HttpStatus.OK);
    }

    @GetMapping("/public/movie/search")
    public ResponseEntity<MovieAllResponse> searchMoviesByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam (name = "sort", defaultValue = AppConstants.SORT_TYPE_MOVIE_DEFAULT, required = false) String sort,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "language", required = false) String language,
            @RequestParam(name = "adult", required = false) Boolean adult,
            @RequestParam(name = "voteAverage", required = false) Double voteAverage,
            @RequestParam(name = "popularity", required = false) Double popularity

            ) {
        MovieAllResponse result = this.movieService.getMoviesByKeyword(pageNumber, pageSize, sort, order, keyword,  year, language,  adult, voteAverage, popularity);
        return new ResponseEntity<MovieAllResponse>(result, HttpStatus.OK);
    }

    @GetMapping("/public/movie/{movieId}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long movieId){
        MovieDTO response = this.movieService.getMovieById(movieId);
        return new ResponseEntity<MovieDTO>(response, HttpStatus.OK);
    }

    @PostMapping("/movie/{movieId}/addComment")
    public ResponseEntity<MovieDTO> addComment(
            @PathVariable Long movieId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        MovieDTO response = this.commentService.addCommentToMovie(movieId, commentDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/movie/{movieId}/editComment/{commentId}")
    public ResponseEntity<MovieDTO> editComment(
            @PathVariable Long movieId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        MovieDTO response = this.commentService.editComment(movieId, commentId, commentDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/movie/{movieId}/deleteComment/{commentId}")
    public ResponseEntity<MovieDTO> deleteComment(
            @PathVariable Long movieId,
            @PathVariable Long commentId
    ) {
        MovieDTO response = this.commentService.deleteComment(movieId, commentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
