package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.payload.CreateCommentRequest;
import com.social.horror_pool.repository.CommentRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.security.CustomUserDetails;
import com.social.horror_pool.service.CommentService;
import com.social.horror_pool.service.MovieCommentsMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private final MovieCommentsMapper movieCommentsMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a");

    public CommentServiceImpl(CommentRepository commentRepository, MovieRepository movieRepository, UserRepository userRepository, MovieCommentsMapper movieCommentsMapper) {
        this.commentRepository = commentRepository;
        this.movieRepository = movieRepository;
        this.movieCommentsMapper = movieCommentsMapper;
    }

    @Override
    @Transactional
    public MovieDTO addCommentToMovie(Long movieId, CreateCommentRequest request) {
        User user = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        Comment comment = new Comment();

        comment.setUser(user);
        comment.setMovie(movie);
        comment.setCommentContent(request.getCommentContent());
        comment.setDate(LocalDateTime.now().format(formatter));
        this.commentRepository.save(comment);

       return movieCommentsMapper.returnMovieWithComments(movie);
    }

    @Override
    @Transactional
    public MovieDTO replyToComment(Long movieId, Long commentId, CreateCommentRequest request) {
        User user = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!Objects.equals(parentComment.getMovie().getMovieId(), movieId)) {
            throw new APIException("Comment does not belong to this movie");
        }

        Comment reply = new Comment();

        reply.setUser(user);
        reply.setMovie(movie);
        reply.setCommentContent(request.getCommentContent());
        reply.setDate(LocalDateTime.now().format(formatter));
        reply.setParentComment(parentComment);
        this.commentRepository.save(reply);

        return movieCommentsMapper.returnMovieWithComments(movie);
    }

    @Override
    @Transactional
    public MovieDTO editComment(Long movieId, Long commentId, CreateCommentRequest request) {
        User user = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!Objects.equals(comment.getUser().getUserId(), user.getUserId())) {
            throw new APIException("You are not authorized to edit this comment");
        }

        if (!Objects.equals(comment.getMovie().getMovieId(), movieId)) {
            throw new APIException("Comment does not belong to the specified movie");
        }

        comment.setCommentContent(request.getCommentContent());
        this.commentRepository.save(comment);

        return movieCommentsMapper.returnMovieWithComments(movie);
    }

    @Override
    @Transactional
    public MovieDTO deleteComment(Long movieId, Long commentId) {
        User user = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!Objects.equals(comment.getUser().getUserId(), user.getUserId()) && user.getUserId() != 1L) {
            throw new APIException("You are not authorized to delete this comment");
        }

        if (!Objects.equals(comment.getMovie().getMovieId(), movieId)) {
            throw new APIException("Comment does not belong to the specified movie");
        }

        this.commentRepository.delete(comment);

        return movieCommentsMapper.returnMovieWithComments(movie);

    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        return customUserDetails.getUser();
    }

}
