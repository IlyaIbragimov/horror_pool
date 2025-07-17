package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1, movie2, movie3;

    private MovieDTO dto1, dto2, dto3;

    @BeforeEach
    public void setUp() {

        movie1 = createMovie(1L, "Alien", false);
        movie2 = createMovie(2L, "Hereditary", false);
        movie3 = createMovie(3L, "The Babadook", true);


        dto1 = createMovieDTO(movie1);
        dto2 = createMovieDTO(movie2);
        dto3 = createMovieDTO(movie3);
    }

    @Test
    public void testGetAllMovies_ReturnsAllMoviesPaged() {

        List<Movie> movieList = Arrays.asList(movie1, movie2, movie3);
        Page<Movie> moviePage = new PageImpl<>(movieList);

        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(modelMapper.map(eq(movie1), eq(MovieDTO.class))).thenReturn(dto1);
        when(modelMapper.map(eq(movie2), eq(MovieDTO.class))).thenReturn(dto2);
        when(modelMapper.map(eq(movie3), eq(MovieDTO.class))).thenReturn(dto3);

        int pageNumber = 0;
        int pageSize = 5;
        String order = "asc";

        MovieAllResponse response = movieService.getAllMovies(pageNumber, pageSize, "title", order);

        assertNotNull(response);
        assertEquals(3, response.getMovies().size());
        assertEquals("Alien", response.getMovies().get(0).getTitle());
        assertEquals("The Babadook", response.getMovies().get(2).getOriginalTitle());

        verify(movieRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, times(3)).map(any(Movie.class), eq(MovieDTO.class));

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

    private MovieDTO createMovieDTO(Movie movie) {
        return new MovieDTO(
                movie.getMovieId(),
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
