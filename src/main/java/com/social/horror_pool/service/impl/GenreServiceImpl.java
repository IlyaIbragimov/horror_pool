package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.service.GenreService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final ModelMapper modelMapper;

    public GenreServiceImpl(GenreRepository genreRepository, ModelMapper modelMapper) {
        this.genreRepository = genreRepository;
        this.modelMapper = modelMapper;
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
}
