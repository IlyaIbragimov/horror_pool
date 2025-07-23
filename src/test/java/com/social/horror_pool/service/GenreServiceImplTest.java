package com.social.horror_pool.service;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.model.Genre;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        genreDTO1 = createGenreDTO(1L, "Thriller");
        genreDTO2 = createGenreDTO(2L, "Body horror");
        genreDTO3 = createGenreDTO(3L, "Classic");
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






    private Genre createGenre(Long genreId, String genreName) {
        Genre genre = new Genre();
        genre.setGenreId(genreId);
        genre.setName(genreName);
        genre.setDescription("Description for " + genreName);
        return genre;
    }

    private GenreDTO createGenreDTO(Long id, String name) {
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setGenreId(id);
        genreDTO.setName(name);
        genreDTO.setDescription("Description for " + name);
        return genreDTO;
    }
}
