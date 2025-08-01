package com.social.horror_pool.service;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.GenreAllResponse;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.impl.GenreServiceImpl;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {
    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private GenreServiceImpl genreServiceImpl;

    private Genre genre1, genre2, genre3;

    private GenreDTO genreDTO1, genreDTO2, genreDTO3;


    @BeforeEach
    public void setUp() {
        genre1 = createGenre(1L, "Thriller");
        genre2 = createGenre(2L, "Body horror");
        genre3 = createGenre(3L, "Classic");

        genreDTO1 = createGenreDTO(genre1);
        genreDTO2 = createGenreDTO(genre2);
        genreDTO3 = createGenreDTO(genre3);
    }

    @Test
    public void addGenre_success_ReturnsGenreDTO() {
        when(genreRepository.findByName(genreDTO1.getName())).thenReturn(null);
        when(modelMapper.map(genreDTO1, Genre.class)).thenReturn(genre1);
        when(genreRepository.save(genre1)).thenReturn(genre1);
        when(modelMapper.map(genre1, GenreDTO.class)).thenReturn(genreDTO1);

        GenreDTO result = genreServiceImpl.addGenre(genreDTO1);

        assertNotNull(result);
        assertEquals(genreDTO1, result);
        verify(genreRepository, times(1)).findByName(genreDTO1.getName());
        verify(genreRepository, times(1)).save(genre1);
        verify(modelMapper, times(1)).map(genreDTO1, Genre.class);
        verify(modelMapper, times(1)).map(genre1, GenreDTO.class);
    }

    @Test
    public void addGenre_GenreAlreadyExists_ReturnsAPIException() {
        when(genreRepository.findByName(genreDTO1.getName())).thenReturn(genre1);

        APIException exception = assertThrows(APIException.class, () -> genreServiceImpl.addGenre(genreDTO1));

        assertTrue(exception.getMessage().contains(genreDTO1.getName()));
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void getAllGenres__ReturnsGenreAllResponse_AscOrder() {
        List<Genre> genreList = Arrays.asList(genre1, genre2, genre3);
        Page<Genre> genrePage = new PageImpl<>(genreList);

        when(genreRepository.findAll(any(Pageable.class))).thenReturn(genrePage);
        when(modelMapper.map(eq(genre1), eq(GenreDTO.class))).thenReturn(genreDTO1);
        when(modelMapper.map(eq(genre2), eq(GenreDTO.class))).thenReturn(genreDTO2);
        when(modelMapper.map(eq(genre3), eq(GenreDTO.class))).thenReturn(genreDTO3);

        GenreAllResponse result = genreServiceImpl.getAllGenres(0, 3,  "asc");

        assertNotNull(result);
        assertEquals(3, result.getGenres().size());
        assertEquals(genreDTO1, result.getGenres().get(0));
        assertEquals(genreDTO2, result.getGenres().get(1));
        assertEquals(genreDTO3, result.getGenres().get(2));

        verify(genreRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, times(3)).map(any(Genre.class), eq(GenreDTO.class));
    }

    @Test
    public void getAllGenres__ReturnsGenreAllResponse_DescOrder() {
        List<Genre> genreList = Arrays.asList(genre3, genre2, genre1);
        Page<Genre> genrePage = new PageImpl<>(genreList);

        when(genreRepository.findAll(any(Pageable.class))).thenReturn(genrePage);
        when(modelMapper.map(eq(genre1), eq(GenreDTO.class))).thenReturn(genreDTO1);
        when(modelMapper.map(eq(genre2), eq(GenreDTO.class))).thenReturn(genreDTO2);
        when(modelMapper.map(eq(genre3), eq(GenreDTO.class))).thenReturn(genreDTO3);

        GenreAllResponse result = genreServiceImpl.getAllGenres(0, 3,  "desc");

        assertNotNull(result);
        assertEquals(3, result.getGenres().size());
        assertEquals(genreDTO1, result.getGenres().get(2));
        assertEquals(genreDTO2, result.getGenres().get(1));
        assertEquals(genreDTO3, result.getGenres().get(0));

        verify(genreRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, times(3)).map(any(Genre.class), eq(GenreDTO.class));
    }

    @Test
    public void editGenre_UpdateExistingGenre_Success_ReturnsGenreDTO() {
        GenreDTO updateDTO = createGenreDTO(genre3);
        updateDTO.setDescription("Updated");
        updateDTO.setName("Updated");

        when(genreRepository.findById(updateDTO.getGenreId())).thenReturn(Optional.of(genre3));
        when(genreRepository.findByName(updateDTO.getName())).thenReturn(genre3);
        when(genreRepository.save(genre3)).thenReturn(genre3);
        when(modelMapper.map(eq(genre3), eq(GenreDTO.class))).thenReturn(updateDTO);

        GenreDTO result = genreServiceImpl.editGenre(updateDTO, 3L);

        assertNotNull(result);
        assertEquals(updateDTO.getGenreId(), result.getGenreId());
        assertEquals(updateDTO.getDescription(), result.getDescription());
        assertEquals(updateDTO.getName(), result.getName());
        verify(genreRepository, times(1)).findById(updateDTO.getGenreId());
        verify(genreRepository, times(1)).save(genre3);
        verify(modelMapper).map(genre3, GenreDTO.class);
        verify(genreRepository, times(1)).findByName(updateDTO.getName());
    }

    @Test
    public void editGenre_NotExistingGenre_ReturnsResourceNotFoundException() {
        Long NotExistingGenreId = 5L;
        when(genreRepository.findById(NotExistingGenreId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.editGenre(genreDTO3, NotExistingGenreId));
        assertEquals("Genre was not found with genreId : 5", exception.getMessage());
        verify(genreRepository, times(1)).findById(NotExistingGenreId);
    }

    @Test
    public void editGenre_GenreAlreadyExists_ReturnsAPIException() {
        GenreDTO updateDTO = createGenreDTO(genre3);
        updateDTO.setName("Body horror");

        when(genreRepository.findById(updateDTO.getGenreId())).thenReturn(Optional.of(genre3));
        when(genreRepository.findByName(updateDTO.getName())).thenReturn(genre2);

        APIException exception = assertThrows(APIException.class, () -> genreServiceImpl.editGenre(updateDTO, 3L));

        assertTrue(exception.getMessage().contains(updateDTO.getName()));
        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(genre2.getName(), updateDTO.getName());
        verify(genreRepository, times(1)).findByName(updateDTO.getName());
        verify(genreRepository, times(1)).findById(updateDTO.getGenreId());
    }

    @Test
    public void deleteGenre_Success_ReturnsGenreDTO() {
        Movie movie = createMovie(1L, "Test");
        movie.setGenres(new ArrayList<>(Arrays.asList(genre1, genre2, genre3)));
        genre1.setMovies(new ArrayList<>(Collections.singletonList(movie)));

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre1));
        when(movieRepository.save(movie)).thenReturn(movie);

        doNothing().when(genreRepository).delete(genre1);
        when(modelMapper.map(genre1, GenreDTO.class)).thenReturn(genreDTO1);

        GenreDTO result = genreServiceImpl.deleteGenre(1L);

        assertNotNull(result);
        assertEquals(genreDTO1, result);
        verify(genreRepository, times(1)).findById(1L);
        verify(genreRepository, times(1)).delete(genre1);
        verify(movieRepository, times(1)).save(movie);

        assertFalse(movie.getGenres().contains(genre1));
    }
    
    private Genre createGenre(Long genreId, String genreName) {
        Genre genre = new Genre();
        genre.setGenreId(genreId);
        genre.setName(genreName);
        genre.setDescription("Description for " + genreName);
        return genre;
    }

    private GenreDTO createGenreDTO(Genre genre) {
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setGenreId(genre.getGenreId());
        genreDTO.setName(genre.getName());
        genreDTO.setDescription("Description for " + genre.getName());
        return genreDTO;
    }

    private Movie createMovie(Long id, String title) {
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
        movie.setAdult(false);
        movie.setVideo(false);
        movie.setGenres(new ArrayList<>());
        movie.setWatchlistItems(new ArrayList<>());
        movie.setComments(new ArrayList<>());
        return movie;
    }
}
