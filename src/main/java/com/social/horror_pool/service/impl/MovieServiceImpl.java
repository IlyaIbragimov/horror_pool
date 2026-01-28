package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.dto.tmdb.TmdbDiscoverMovieDTO;
import com.social.horror_pool.dto.tmdb.TmdbMovieDTO;
import com.social.horror_pool.enums.MovieSortField;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.payload.tmdb.BulkImportResultResponse;
import com.social.horror_pool.payload.tmdb.TmdbDiscoverMovieAllResponse;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.MovieCommentsMapper;
import com.social.horror_pool.service.MovieService;
import com.social.horror_pool.tmdb.TmdbClient;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final GenreRepository genreRepository;
    private final TmdbClient tmdbClient;
    private final MovieCommentsMapper movieCommentsMapper;

    public MovieServiceImpl(MovieRepository movieRepository, ModelMapper modelMapper, GenreRepository genreRepository, TmdbClient tmdbClient, MovieCommentsMapper movieCommentsMapper) {
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.tmdbClient = tmdbClient;
        this.movieCommentsMapper = movieCommentsMapper;
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

        if(!MovieSortField.isValidField(sortBy)) throw new APIException("Invalid sort field");

        Sort sortAndOrder = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);

        Page<Movie> page = this.movieRepository.findAll(pageable);

        return generateMovieAllResponse(page, pageNumber, pageSize);
    }

    @Override
    @Transactional
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
        movieToEdit.setReleaseYear(movieDTO.getReleaseYear());
        movieToEdit.setPosterPath(movieDTO.getPosterPath());
        movieToEdit.setBackdropPath(movieDTO.getBackdropPath());
        movieToEdit.setVoteAverage(movieDTO.getVoteAverage());
        movieToEdit.setVoteCount(movieDTO.getVoteCount());
        movieToEdit.setPopularity(movieDTO.getPopularity());
        movieToEdit.setOriginalLanguage(movieDTO.getOriginalLanguage());
        movieToEdit.setAdult(movieDTO.getAdult());
        movieToEdit.setVideo(movieDTO.getVideo());

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
                    .map(genreMapByGenresIds::get)
                    .toList();

            movieToEdit.getGenres().clear();
            movieToEdit.getGenres().addAll(genresToAdd);
        }

        this.movieRepository.save(movieToEdit);

        return this.modelMapper.map(movieToEdit, MovieDTO.class);
    }

    @Override
    @Transactional
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

    @Override
    public MovieAllResponse getMoviesByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String order, String keyword, Integer year, String language, Boolean adult, Double voteAverage, Double popularity) {

        if(!MovieSortField.isValidField(sortBy)) throw new APIException("Invalid sort field");

        Sort sortAndOrder = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortAndOrder);

        Specification<Movie> filters = filterMovies(year,language,adult,voteAverage,popularity, keyword);

        Page<Movie> page = this.movieRepository.findAll(filters, pageable);

        return generateMovieAllResponse(page, pageNumber, pageSize);
    }

    @Override
    public MovieDTO getMovieById(Long movieId) {

        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", movieId));

        return this.movieCommentsMapper.returnMovieWithComments(movie);
    }

    @Override
    public MovieDTO getMovieByTmdbId(Long tmdbId) {
        Movie movie = this.movieRepository.findByTmdbId(tmdbId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "tmdbId", tmdbId));
        return this.modelMapper.map(movie, MovieDTO.class);
    }

    @Override
    public boolean checkIftmdbIdExists(Long tmdbId) {
        return this.movieRepository.existsByTmdbId(tmdbId);
    }

    @Override
    @Transactional
    public MovieDTO importFromTmdb(Long tmdbId, String language) {

        if (movieRepository.existsByTmdbId(tmdbId)) {
            throw new APIException("Movie with tmdbID: " + tmdbId + " already exists");
        }

        TmdbMovieDTO tmdbDTO = tmdbClient.getMovieById(tmdbId, language == null ? "en-US" : language)
                .orElseThrow(() -> new APIException("TMDB movie not found (tmdbId= " + tmdbId + ")"));

        Movie movie = new Movie();
        movie.setTmdbId(tmdbDTO.getId());
        movie.setTitle(tmdbDTO.getTitle());
        movie.setOverview(tmdbDTO.getOverview());
        movie.setPosterPath(tmdbDTO.getPosterPath());
        movie.setBackdropPath(tmdbDTO.getBackdropPath());
        movie.setOriginalLanguage(tmdbDTO.getOriginalLanguage());
        movie.setVoteAverage(tmdbDTO.getVoteAverage());
        movie.setVoteCount(tmdbDTO.getVoteCount());
        movie.setPopularity(tmdbDTO.getPopularity());
        movie.setAdult(Boolean.TRUE.equals(tmdbDTO.getAdult()));
        movie.setVideo(Boolean.TRUE.equals(tmdbDTO.getVideo()));

        LocalDate rd = tmdbDTO.getReleaseDate();
        if (rd != null) {
            movie.setReleaseDate(rd);
            movie.setReleaseYear(rd.getYear());
        }

        Movie saved = this.movieRepository.save(movie);
        return this.modelMapper.map(saved, MovieDTO.class);
    }

    @Override
    @Transactional
    public BulkImportResultResponse bulkImportFromTmdb(Integer pages, String language) {
        int pagesToImport = (pages == null || pages < 1) ? 1 : pages;
        String languageToImport = (language == null || language.isBlank()) ? "en-US" : language;

        BulkImportResultResponse response = new BulkImportResultResponse();

        for (int p = 1; p <= pagesToImport; p++) {
            TmdbDiscoverMovieAllResponse discoverResponse = this.tmdbClient.discoverHorrors(p, languageToImport);
            if (discoverResponse == null || discoverResponse.getResults() == null) continue;

            for (TmdbDiscoverMovieDTO movie : discoverResponse.getResults()) {
                if (movie == null || movie.getId() == null) continue;
                Long tmdbId = movie.getId();
                try {
                    if (this.movieRepository.existsByTmdbId(tmdbId)) {
                        response.setSkipped(response.getSkipped() + 1);
                        continue;
                    }
                    this.importFromTmdb(tmdbId, languageToImport);
                    response.setImported(response.getImported() + 1);
                } catch (Exception ex) {
                    response.setFailed(response.getFailed() + 1);
                    response.getErrors().add("tmdbId = " + tmdbId + " : " + ex.getMessage());
                }
            }
        }
        return response;
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

    private Specification<Movie> filterMovies(Integer year, String language, Boolean adult, Double voteAverage, Double popularity, String keyword) {

        Specification<Movie> filters = Specification.where(null);

        if (year != null) {
           filters = filters.and((root, query, criteriaBuilder) ->
                   criteriaBuilder.equal(root.get("releaseYear"), year));
        }

        if (language != null && !language.isBlank()) {
            filters = filters.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("originalLanguage"), language));
        }

        if (adult != null) {
            filters = filters.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("adult"), adult));
        }

        if (voteAverage != null) {
            filters = filters.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("voteAverage"), voteAverage));
        }

        if (popularity != null) {
            filters = filters.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("popularity"), popularity));
        }

        if (keyword != null && !keyword.isBlank()) {
            filters = filters.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
        }

        return filters;
    }
}
