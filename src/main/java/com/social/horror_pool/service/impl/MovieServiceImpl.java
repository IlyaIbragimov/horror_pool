package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final ModelMapper modelMapper;

    private final GenreRepository genreRepository;

    public MovieServiceImpl(MovieRepository movieRepository, ModelMapper modelMapper, GenreRepository genreRepository) {
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO) {
        Movie movieExistingWithSameTitle = this.movieRepository.findByTitle(movieDTO.getTitle());
        if (movieExistingWithSameTitle != null && movieExistingWithSameTitle.getReleaseDate().equals(movieDTO.getReleaseDate()))
            throw new APIException("The movie " + movieDTO.getTitle() + " " + "(" + movieDTO.getReleaseDate() + ")" + " already exists.");
        Movie addedMovie = this.modelMapper.map(movieDTO, Movie.class);
        this.movieRepository.save(addedMovie);
        return this.modelMapper.map(addedMovie, MovieDTO.class);
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> allMovies = this.movieRepository.findAll();
        if (allMovies.isEmpty()) throw new APIException("No movies available");
        return allMovies.stream()
                .map(movie -> this.modelMapper.map(movie, MovieDTO.class)).toList();
    }

    @Override
    public MovieDTO editMovie(MovieDTO movieDTO, Long movieId) {

        Movie movieToEdit = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", movieId));

        Movie movieExistingWithSameTitle = this.movieRepository.findByTitle(movieDTO.getTitle());
        if (movieExistingWithSameTitle != null && movieExistingWithSameTitle.getReleaseDate().equals(movieDTO.getReleaseDate())
                && !movieExistingWithSameTitle.getMovieId().equals(movieId))
            throw new APIException("The movie " + movieDTO.getTitle() + " " + "(" + movieDTO.getReleaseDate() + ")" + " already exists.");

        movieToEdit.setTitle(movieDTO.getTitle());
        movieToEdit.setOriginalTitle(movieDTO.getOriginalTitle());
        movieToEdit.setDescription(movieDTO.getDescription());
        movieToEdit.setOverview(movieDTO.getOverview());
        movieToEdit.setReleaseDate(movieDTO.getReleaseDate());
        movieToEdit.setPosterPath(movieDTO.getPosterPath());
        movieToEdit.setBackdropPath(movieDTO.getBackdropPath());
        movieToEdit.setVoteAverage(movieDTO.getVoteAverage());
        movieToEdit.setVoteCount(movieDTO.getVoteCount());
        movieToEdit.setPopularity(movieDTO.getPopularity());
        movieToEdit.setOriginalLanguage(movieDTO.getOriginalLanguage());
        movieToEdit.setAdult(movieDTO.getAdult());
        movieToEdit.setVideo(movieDTO.getVideo());

        if (!movieDTO.getGenres().isEmpty()) {
            List<Genre> genresEdited = movieDTO.getGenres().stream()
                    .map(genreDTO -> this.modelMapper.map(genreDTO, Genre.class)).toList();
            movieToEdit.getGenres().clear();
            movieToEdit.getGenres().addAll(genresEdited);
            };


        this.movieRepository.save(movieToEdit);

        return this.modelMapper.map(movieToEdit, MovieDTO.class);
    }

    @Override
    public MovieDTO deleteMovie(Long movieId) {
        Movie movieToDelete = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", movieId));

        for (Genre genre : movieToDelete.getGenres()) {
            genre.getMovies().remove(movieToDelete);
            this.genreRepository.save(genre);
        }

        movieToDelete.getGenres().clear();
        this.movieRepository.delete(movieToDelete);
        return this.modelMapper.map(movieToDelete, MovieDTO.class);
    }


}
