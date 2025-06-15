package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.repository.CommentRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.security.CustomUserDetails;
import com.social.horror_pool.service.CommentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(CommentRepository commentRepository, MovieRepository movieRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public MovieDTO addCommentToMovie(Long movieId, CommentDTO commentDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User user = customUserDetails.getUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        Comment comment = new Comment();

        comment.setUser(user);
        comment.setMovie(movie);
        comment.setCommentContent(commentDTO.getCommentContent());
        comment.setDate(LocalDateTime.now());
        this.commentRepository.save(comment);

        List<CommentDTO> commentDTOS = this.commentRepository.findByMovie(movie).stream()
                .map(com -> {
                    CommentDTO dto = modelMapper.map(com, CommentDTO.class);
                    dto.setUserName(com.getUser().getUsername());
                    return dto;
                }).toList();

        MovieDTO response = this.modelMapper.map(movie, MovieDTO.class);

        response.setComments(commentDTOS);

        return response;
    }

}
