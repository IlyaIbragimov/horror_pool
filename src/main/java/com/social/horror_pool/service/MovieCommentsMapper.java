package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.model.Movie;

public interface MovieCommentsMapper {

    MovieDTO returnMovieWithComments(Movie movie);
}
