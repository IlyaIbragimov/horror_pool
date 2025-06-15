package com.social.horror_pool.service;

import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import jakarta.validation.Valid;

public interface CommentService {
    MovieDTO addCommentToMovie(Long movieId, @Valid CommentDTO commentDTO);

    MovieDTO editComment(Long movieId, Long commentId, @Valid CommentDTO commentDTO);
}
