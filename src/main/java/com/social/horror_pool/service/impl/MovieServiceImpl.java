package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final ModelMapper modelMapper;

    public MovieServiceImpl(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO) {
        Movie movieExistingWithSameTitle = this.movieRepository.findByTitle(movieDTO.getTitle());
        if (movieExistingWithSameTitle != null) throw new APIException("Movie with the title " + movieDTO.getTitle() + " already exists.");
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
}
