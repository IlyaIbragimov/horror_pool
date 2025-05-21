package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.GenreDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Genre;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.payload.GenreAllResponse;
import com.social.horror_pool.repository.GenreRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.service.GenreService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public GenreAllResponse getAllGenres(Integer pageNumber, Integer pageSize, String sort, String order) {

        Sort sortByAndOrder = order.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Genre> page = this.genreRepository.findAll(pageable);

        List<Genre> genresSorted = page.getContent();

        if (genresSorted.isEmpty()) throw new APIException("No genres found");

        List<GenreDTO> genreDTOS = genresSorted.stream()
                .map(genre -> this.modelMapper.map(genre,GenreDTO.class)).toList();

        GenreAllResponse response = new GenreAllResponse();
        response.setGenres(genreDTOS);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        response.setTotalElements(page.getTotalElements());

        return response;
    }


    @Override
    public GenreDTO editGenre(GenreDTO genreDTO, Long genreId) {

        Genre genreToUpdate = this.genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "genreId", genreId));

        Genre existingGenreWithSameName = this.genreRepository.findByName(genreDTO.getName());
        if (existingGenreWithSameName != null && !existingGenreWithSameName.getGenreId().equals(genreId))
            throw new APIException("Genre with the name " + existingGenreWithSameName.getName() + " already exists");

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
