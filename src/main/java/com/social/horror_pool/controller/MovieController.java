package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.service.CommentService;
import com.social.horror_pool.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movie", description = "Endpoints for managing movies")
@RestController
@RequestMapping("/horrorpool")
public class MovieController {

    private final MovieService movieService;
    private final CommentService commentService;

    public MovieController(MovieService movieService, CommentService commentService) {
        this.movieService = movieService;
        this.commentService = commentService;
    }

    @Operation(
            summary = "Create a new movie in the database",
            description = "Create a new movie in the database. Available only for administrator"
    )
    @PostMapping("/admin/movie/add")
    public ResponseEntity<MovieDTO> addMovie(@Valid @RequestBody MovieDTO movieDTO){
        MovieDTO result = this.movieService.addMovie(movieDTO);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Return all the movies from the database",
            description = "Return all the movies from the database. Available for everyone"
    )
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

    @Operation(
            summary = "Search the movie",
            description = "Return the movies depending on the input parameters. Available for everyone"
    )
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

    @Operation(
            summary = "Return the movie by it's id",
            description = "Return the movie by it's id. Available for everyone"
    )
    @GetMapping("/public/movie/{movieId}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long movieId){
        MovieDTO response = this.movieService.getMovieById(movieId);
        return new ResponseEntity<MovieDTO>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Add a comment to the movie",
            description = "Add a comment to the movie. Available for authenticated user"
    )
    @PostMapping("/movie/{movieId}/addComment")
    public ResponseEntity<MovieDTO> addComment(
            @PathVariable Long movieId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        MovieDTO response = this.commentService.addCommentToMovie(movieId, commentDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Reply to the comment",
            description = "Reply to the comment. Available for authenticated user who left the comment"
    )
    @PostMapping("movie/{movieId}/comment/{commentId}/reply}")
    public ResponseEntity<MovieDTO> replyToComment(
            @PathVariable Long movieId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        MovieDTO response = this.commentService.replyToComment(movieId, commentId, commentDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit a comment to the movie",
            description = "Edit a comment to the movie. Available for authenticated user who left the comment"
    )
    @PutMapping("/movie/{movieId}/editComment/{commentId}")
    public ResponseEntity<MovieDTO> editComment(
            @PathVariable Long movieId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        MovieDTO response = this.commentService.editComment(movieId, commentId, commentDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a comment to the movie",
            description = "Delete a comment to the movie. Available for authenticated user who left the comment and administrator"
    )
    @DeleteMapping("/movie/{movieId}/deleteComment/{commentId}")
    public ResponseEntity<MovieDTO> deleteComment(
            @PathVariable Long movieId,
            @PathVariable Long commentId
    ) {
        MovieDTO response = this.commentService.deleteComment(movieId, commentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit the existing movie",
            description = "Edit the existing movie. Available only for administrator"
    )
    @PutMapping("/admin/movie/{movieId}/edit")
    public ResponseEntity<MovieDTO> editMovie(@Valid @RequestBody MovieDTO movieDTO, @PathVariable Long movieId){
        MovieDTO result = this.movieService.editMovie(movieDTO,movieId);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete the existing movie",
            description = "Delete the existing movie. Available only for administrator"
    )
    @DeleteMapping("/admin/movie/{movieId}/delete")
    public ResponseEntity<MovieDTO> deleteMovie(@PathVariable Long movieId){
        MovieDTO result = this.movieService.deleteMovie(movieId);
        return new ResponseEntity<MovieDTO>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Return the movie by it's tmdbId",
            description = "Return the movie by it's tmdbId. Available for admin only"
    )
    @GetMapping("/admin/movie/{tmdbId}")
    public ResponseEntity<MovieDTO> getMovieByTmdbId(@PathVariable Long tmdbId){
        MovieDTO response = this.movieService.getMovieByTmdbId(tmdbId);
        return new ResponseEntity<MovieDTO>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Return if the movie exists in DB by it's tmdbId",
            description = "Return if the movie exists in DB by it's tmdbId. Available for admin only"
    )
    @GetMapping("admin/movie/exists/{tmbdId}")
    public boolean ifTmdbIdExists(@PathVariable Long tmdbId) {
        return this.movieService.checkIftmdbIdExists(tmdbId);
    }
}
