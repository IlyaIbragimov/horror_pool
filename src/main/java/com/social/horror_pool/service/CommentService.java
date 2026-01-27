package com.social.horror_pool.service;

import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.payload.CreateCommentRequest;
import jakarta.validation.Valid;

public interface CommentService {
    MovieDTO addCommentToMovie(Long movieId, @Valid CreateCommentRequest request);

    MovieDTO editComment(Long movieId, Long commentId, @Valid CreateCommentRequest request);

    MovieDTO deleteComment(Long movieId, Long commentId);

    MovieDTO replyToComment(Long movieId, Long commentId, @Valid CreateCommentRequest request);
}
