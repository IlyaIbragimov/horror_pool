package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public MovieAllResponse getAllMovies(Integer pageNumber, Integer pageSize, String sortBy, String order) {

        Sort sortAndOrder = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);

        Page<Movie> page = this.movieRepository.findAll(pageable);

        return generateMovieAllResponse(page, pageNumber, pageSize);

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

//        if (!movieDTO.getGenres().isEmpty()) {

//            movieDTO.getGenres().forEach(genre -> {
////                if (this.genreRepository.findById(genre.getGenreId()).isEmpty())
////                    throw new ResourceNotFoundException("Genre", "genreId", genre.getGenreId());
////
////            });
////
////
////            List<Genre> genresEdited = movieDTO.getGenres().stream()
////                    .map(genreDTO -> this.modelMapper.map(genreDTO, Genre.class)).toList();
////            movieToEdit.getGenres().clear();
////            movieToEdit.getGenres().addAll(genresEdited);
////            };

        if (!movieDTO.getGenres().isEmpty()) {

            List<Long> genresId = movieDTO.getGenres().stream()
                    .map(GenreDTO::getGenreId).toList();

            Map<Long, Genre> genreMapByGenresIds = this.genreRepository.findAllById(genresId).stream()
                    .collect(Collectors.toMap(Genre::getGenreId, genre -> genre));

            for (Long genreId : genresId) {
                if (!genreMapByGenresIds.containsKey(genreId)) {
                    throw new ResourceNotFoundException("Genre", "genreId", genreId);
                }
            }

            List<Genre> genresToAdd = genresId.stream()
                    .map(genreMapByGenresIds :: get)
                    .toList();

            movieToEdit.getGenres().clear();
            movieToEdit.getGenres().addAll(genresToAdd);
        }
        
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

    private MovieAllResponse generateMovieAllResponse(Page<Movie> page, Integer pageNumber, Integer pageSize){

        List<Movie> moviesSorted = page.getContent();

        List<MovieDTO> movieDTOS = moviesSorted.stream()
                .map(movie -> this.modelMapper.map(movie, MovieDTO.class)).toList();

        MovieAllResponse response = new MovieAllResponse();
        response.setMovies(movieDTOS);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLastPage(page.isLast());
        return response;


    }


}
