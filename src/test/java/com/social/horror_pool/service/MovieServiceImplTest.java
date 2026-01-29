package com.social.horror_pool.service;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.MovieAllResponse;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MovieCommentsMapper movieCommentsMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1, movie2, movie3;

    private MovieDTO dto1, dto2, dto3;

    private Genre genre1, genre2;

    private GenreDTO genreDTO1, genreDTO2;

    @BeforeEach
    public void setUp() {
        movie1 = createMovie(1L, "Alien", false);
        movie2 = createMovie(2L, "Hereditary", false);
        movie3 = createMovie(3L, "The Babadook", true);

        genre1 = createGenre(1L, "Horror");
        genre2 = createGenre(2L, "Sci-Fi");
        genreDTO1 = createGenreDTO(1L, "Horror");
        genreDTO2 = createGenreDTO(2L, "Sci-Fi");

        movie1.setGenres(new ArrayList<>(Arrays.asList(genre1, genre2)));
        movie2.setGenres(new ArrayList<>(Collections.singletonList(genre1)));

        genre1.setMovies(new ArrayList<>(Arrays.asList(movie1, movie2)));
        genre2.setMovies(new ArrayList<>(Collections.singletonList(movie1)));

        dto1 = createMovieDTO(movie1);
        dto1.setGenres(Arrays.asList(genreDTO1, genreDTO2));
        dto2 = createMovieDTO(movie2);
        dto2.setGenres(Collections.singletonList(genreDTO1));
        dto3 = createMovieDTO(movie3);
    }

    @Test
    public void addMovie_Success() {
        when(movieRepository.findByTitle(dto1.getTitle())).thenReturn(null);
        when(modelMapper.map(eq(dto1), eq(Movie.class))).thenReturn(movie1);
        when(movieRepository.save(movie1)).thenReturn(movie1);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);

        MovieDTO result = movieService.addMovie(dto1);
        assertNotNull(result);
        assertEquals(dto1, result);
        verify(movieRepository, times(1)).findByTitle(dto1.getTitle());
        verify(movieRepository, times(1)).save(movie1);
        verify(modelMapper, times(1)).map(eq(dto1), eq(Movie.class));
        verify(modelMapper, times(1)).map(eq(movie1), eq(MovieDTO.class));
    }

    @Test
    public void addMovie_MovieWithTheSameTitleAndReleaseDateExists_APIException() {
        when(movieRepository.findByTitle(dto1.getTitle())).thenReturn(movie1);

        APIException exception = assertThrows(APIException.class, () -> movieService.addMovie(dto1));

        assertTrue(exception.getMessage().contains(dto1.getTitle()));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(movieRepository, times(1)).findByTitle(dto1.getTitle());
        verify(movieRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    public void addMovie_MovieWithTheSameTitleButDifferentReleaseDateExists_Success() {
        Movie movieWithTheSameTitleButAnotherDate = createMovie(4L, movie1.getTitle(), false);

        when(movieRepository.findByTitle(dto1.getTitle())).thenReturn(movieWithTheSameTitleButAnotherDate);
        when(modelMapper.map(eq(dto1), eq(Movie.class))).thenReturn(movie1);
        when(movieRepository.save(movie1)).thenReturn(movie1);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);

        MovieDTO result = movieService.addMovie(dto1);
        assertNotNull(result);
        assertEquals(dto1, result);
        verify(movieRepository, times(1)).findByTitle(dto1.getTitle());
        verify(modelMapper, times(1)).map(eq(dto1), eq(Movie.class));
        verify(movieRepository, times(1)).save(movie1);
        verify(modelMapper, times(1)).map(eq(movie1), eq(MovieDTO.class));
    }

    @Test
    public void testGetAllMovies_ReturnsAllMoviesPaged_AscOrder() {
        List<Movie> movieList = Arrays.asList(movie1, movie2, movie3);
        Page<Movie> moviePage = new PageImpl<>(movieList);

        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);
        when(modelMapper.map(eq(movie2), eq(MovieDTO.class))).thenReturn(dto2);
        when(modelMapper.map(eq(movie3), eq(MovieDTO.class))).thenReturn(dto3);

        MovieAllResponse response = movieService.getAllMovies(0, 5, "title", "asc");

        assertNotNull(response);
        assertEquals(3, response.getMovies().size());
        assertEquals("Alien", response.getMovies().get(0).getTitle());
        assertEquals("The Babadook", response.getMovies().get(2).getTitle());

        verify(movieRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, times(3)).map(any(Movie.class), eq(MovieDTO.class));
    }

    @Test
    public void testGetAllMovies_ReturnsAllMoviesPaged_DescOrder() {
        List<Movie> movieList = Arrays.asList(movie3, movie2, movie1);
        Page<Movie> moviePage = new PageImpl<>(movieList);

        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);
        when(modelMapper.map(eq(movie2), eq(MovieDTO.class))).thenReturn(dto2);
        when(modelMapper.map(eq(movie3), eq(MovieDTO.class))).thenReturn(dto3);

        MovieAllResponse response = movieService.getAllMovies(0, 5, "title", "desc");

        assertNotNull(response);
        assertEquals(3, response.getMovies().size());
        assertEquals("Alien", response.getMovies().get(2).getTitle());
        assertEquals("Hereditary", response.getMovies().get(1).getTitle());
        assertEquals("The Babadook", response.getMovies().get(0).getTitle());

        verify(movieRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, times(3)).map(any(Movie.class), eq(MovieDTO.class));
    }

    @Test
    public void testGetAllMovies_ThrowsAPIExceptionWhenSortIsInvalid() {
        APIException exception = assertThrows(APIException.class, () -> movieService.getAllMovies(0, 5, "invalidField", "asc"));

        assertEquals("Invalid sort field", exception.getMessage());
    }

    @Test
    public void editMovie_NoDuplicateTitleFound_GenresAreFound_ReturnsUpdatedMovieDto() {
        MovieDTO updatedMovieDTO = createMovieDTO(movie1);
        updatedMovieDTO.setTitle("Updated");
        updatedMovieDTO.setReleaseDate(LocalDate.of(2020,1,1));
        updatedMovieDTO.setGenres(Collections.singletonList(genreDTO1));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.findByTitle(updatedMovieDTO.getTitle())).thenReturn(null);
        when(genreRepository.findAllById(anyList())).thenReturn(Collections.singletonList(genre1));
        when(movieRepository.save(movie1)).thenReturn(movie1);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(updatedMovieDTO);

        MovieDTO result = movieService.editMovie(updatedMovieDTO, 1L);

        assertNotNull(result);
        assertEquals("Updated", result.getTitle());
        assertEquals(updatedMovieDTO.getReleaseDate(), result.getReleaseDate());
        assertEquals(1, result.getGenres().size());
        verify(movieRepository).save(movie1);
        verify(modelMapper).map(movie1, MovieDTO.class);
    }

    @Test
    public void editMovie_MovieNotFound_ReturnsResourceNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.editMovie(dto1, 1L));
        assertEquals("Movie was not found with movieId : 1", exception.getMessage());
    }

    @Test
    public void editMovie_DuplicateTitleWithSameReleaseDateFound_ReturnsApiException() {
        MovieDTO updatedMovieDTO = createMovieDTO(movie1);
        updatedMovieDTO.setTitle("Hereditary");
        updatedMovieDTO.setReleaseDate(LocalDate.of(2002,1,1));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.findByTitle(updatedMovieDTO.getTitle())).thenReturn(movie2);
        APIException exception = assertThrows(APIException.class, () -> movieService.editMovie(updatedMovieDTO, 1L));

        assertTrue(exception.getMessage().contains("already exists."));
        assertNotEquals(1L, movie2.getMovieId());
        assertEquals(movie2.getReleaseDate(), updatedMovieDTO.getReleaseDate());
    }

    @Test
    public void editMovie_DuplicateTitleWithDifferentReleaseDateFound_Success() {
        MovieDTO updatedMovieDTO = createMovieDTO(movie1);
        updatedMovieDTO.setTitle("Hereditary");
        updatedMovieDTO.setReleaseDate(LocalDate.of(2008,1,1));
        updatedMovieDTO.setGenres(Arrays.asList(genreDTO1, genreDTO2));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.findByTitle(updatedMovieDTO.getTitle())).thenReturn(movie2);
        when(genreRepository.findAllById(anyList())).thenReturn(Arrays.asList(genre1, genre2));
        when(movieRepository.save(movie1)).thenReturn(movie1);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(updatedMovieDTO);

        MovieDTO result = movieService.editMovie(updatedMovieDTO, 1L);

        assertNotNull(result);
        assertEquals("Hereditary", result.getTitle());
        assertEquals(updatedMovieDTO.getReleaseDate(), result.getReleaseDate());
        assertEquals(2, result.getGenres().size());
        assertNotEquals(movie2.getReleaseDate(), updatedMovieDTO.getReleaseDate());

        verify(movieRepository).save(movie1);
        verify(modelMapper).map(movie1, MovieDTO.class);
    }

    @Test
    public void editMovie_GenreNotFound_ReturnsResourceNotFoundException() {
        MovieDTO updatedMovieDTO = createMovieDTO(movie1);
        GenreDTO nonExistingGenreDto = createGenreDTO(3L, "nonExistingGenre");
        updatedMovieDTO.setTitle("Updated");
        updatedMovieDTO.setReleaseDate(LocalDate.of(2008,1,1));
        updatedMovieDTO.setGenres(Arrays.asList(genreDTO1, genreDTO2, nonExistingGenreDto));

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieRepository.findByTitle(updatedMovieDTO.getTitle())).thenReturn(null);
        when(genreRepository.findAllById(anyList())).thenReturn(Arrays.asList(genre1, genre2));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.editMovie(updatedMovieDTO, 1L));

        assertEquals("Genre was not found with genreId : 3", exception.getMessage());
    }

    @Test
    public void editMovie_EditOnlyDescription_Success() {
        MovieDTO updatedMovieDTO = createMovieDTO(movie1);
        updatedMovieDTO.setDescription("updated description");

        when(movieRepository.findById(movie1.getMovieId())).thenReturn(Optional.of(movie1));
        when(movieRepository.findByTitle(movie1.getTitle())).thenReturn(movie1);
        when(movieRepository.save(movie1)).thenReturn(movie1);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(updatedMovieDTO);

        MovieDTO result = movieService.editMovie(updatedMovieDTO, movie1.getMovieId());

        assertNotNull(result);
        assertEquals("updated description", result.getDescription());

        verify(movieRepository).save(movie1);
        verify(modelMapper).map(movie1, MovieDTO.class);
    }

    @Test
    public void deleteMovie_Success_ReturnsDtoOfDeletedMovie() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));

        when(genreRepository.save(genre1)).thenReturn(genre1);
        when(genreRepository.save(genre2)).thenReturn(genre2);

        doNothing().when(movieRepository).delete(movie1);

        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);
        MovieDTO result = movieService.deleteMovie(1L);

        assertNotNull(result);
        assertEquals(dto1, result);

        verify(genreRepository, times(1)).save(genre1);
        verify(genreRepository, times(1)).save(genre2);
        verify(movieRepository, times(1)).delete(movie1);

        assertFalse(genre1.getMovies().contains(movie1));
        assertFalse(genre2.getMovies().contains(movie1));
    }

    @Test
    public void deleteMovie_MovieNotFound_ThrowsResourceNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(1L));
        assertEquals("Movie was not found with movieId : 1", exception.getMessage());
    }

    @Test
    public void getMoviesByKeyword_ValidParams_ReturnsMovieAllResponse() {
        List<Movie> movies = Arrays.asList(movie1);
        Page<Movie> moviePage = new PageImpl<>(movies);
        String sortBy = "title";
        String order = "asc";
        String keyword = "alien";
        Integer year = 2001;
        String language = "en";
        Boolean adult = false;
        Double voteAverage = 8.0;
        Double popularity = 51.0;

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);

        MovieAllResponse response = movieService.getMoviesByKeyword(0, 10, sortBy, order, keyword, year, language, adult, voteAverage, popularity);

        assertNotNull(response);
        assertEquals(1, response.getMovies().size());
        assertEquals("Alien", response.getMovies().getFirst().getTitle());

        verify(movieRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void getMoviesByKeyword_InvalidSortField_ThrowsAPIException() {
        String invalidSortBy = "wrong_field";

        APIException ex = assertThrows(APIException.class, () -> {
            movieService.getMoviesByKeyword(0, 10, invalidSortBy, "asc", "alien", 1979, "en", false, 8.0, 50.0);
        });

        assertEquals("Invalid sort field", ex.getMessage());
    }

    @Test
    public void getMovieById_ReturnsMovieWithTheSameId() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(dto1);

        MovieDTO result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals(dto1, result);

        verify(movieRepository, times(1)).findById(1L);
        verify(movieCommentsMapper, times(1)).returnMovieWithComments(movie1);
    }

    @Test
    public void getMovieById_MovieNotFound_ThrowsResourceNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(1L));
        assertEquals("Movie was not found with movieId : 1", exception.getMessage());

        verify(movieRepository, times(1)).findById(1L);
    }

    private Movie createMovie(Long id, String title, boolean adult) {
        Movie movie = new Movie();
        movie.setMovieId(id);
        movie.setTitle(title);
        movie.setOriginalTitle(title);
        movie.setDescription("Description for " + title);
        movie.setOverview("Overview for " + title);
        movie.setReleaseDate(LocalDate.of(2000 + id.intValue(), 1, 1));
        movie.setReleaseYear(2000 + id.intValue());
        movie.setPosterPath("/" + title.toLowerCase() + "_poster.jpg");
        movie.setBackdropPath("/" + title.toLowerCase() + "_backdrop.jpg");
        movie.setVoteAverage(7.0 + id);
        movie.setVoteCount(1000 * id.intValue());
        movie.setPopularity(50.0 + id);
        movie.setOriginalLanguage("en");
        movie.setAdult(adult);
        movie.setVideo(false);
        movie.setGenres(new ArrayList<>());
        movie.setWatchlistItems(new ArrayList<>());
        movie.setComments(new ArrayList<>());
        return movie;
    }

    private Genre createGenre(Long id, String name) {
        Genre genre = new Genre();
        genre.setGenreId(id);
        genre.setName(name);
        genre.setDescription("Description for " + name);
        return genre;
    }

    private GenreDTO createGenreDTO(Long id, String name) {
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setGenreId(id);
        genreDTO.setName(name);
        genreDTO.setDescription("Description for " + name);
        return genreDTO;
    }

    private MovieDTO createMovieDTO(Movie movie) {
        return new MovieDTO(
                movie.getMovieId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getDescription(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getReleaseYear(),
                movie.getPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.getVoteCount(),
                movie.getPopularity(),
                movie.getOriginalLanguage(),
                movie.getAdult(),
                movie.getVideo(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
