package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.GenreService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    public GenreServiceImpl(GenreRepository genreRepository, ModelMapper modelMapper, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
    }

    @Override
    public GenreDTO addGenre(GenreDTO genreDTO) {
        Genre genreCheck = this.genreRepository.findByName(genreDTO.getName());
        if (genreCheck != null) throw new APIException("Genre " + genreCheck.getName() + " already exists");
        Genre addedGenre = this.modelMapper.map(genreDTO, Genre.class);
        this.genreRepository.save(addedGenre);
        return this.modelMapper.map(addedGenre, GenreDTO.class);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        List<Genre> genres = this.genreRepository.findAll();
        if (genres.isEmpty()) throw new APIException("No genres found");
        return genres.stream().map(genre -> this.modelMapper.map(genre, GenreDTO.class)).toList();
    }


    @Override
    public GenreDTO editGenre(GenreDTO genreDTO, Long genreId) {
        Genre existingGenreWithSameName = this.genreRepository.findByName(genreDTO.getName());
        if (existingGenreWithSameName != null && !existingGenreWithSameName.getGenreId().equals(genreId))
            throw new APIException("Genre with the name " + existingGenreWithSameName.getName() + " already exists");

        Genre genreToUpdate = this.genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "genreId", genreId));

        genreToUpdate.setName(genreDTO.getName());
        genreToUpdate.setDescription(genreDTO.getDescription());

        this.genreRepository.save(genreToUpdate);

        return this.modelMapper.map(genreToUpdate, GenreDTO.class);
    }

    @Override
    public GenreDTO deleteGenre(Long genreId) {
        Genre genreToDelete = this.genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "genreId", genreId));

        for (Movie movie: genreToDelete.getMovies()) {
            movie.getGenres().remove(genreToDelete);
            this.movieRepository.save(movie);
        }

        genreToDelete.getMovies().clear();

        this.genreRepository.delete(genreToDelete);

        return this.modelMapper.map(genreToDelete, GenreDTO.class);
    }
}
