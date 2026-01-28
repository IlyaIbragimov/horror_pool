package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.CommentDTO;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.repository.CommentRepository;
import com.social.horror_pool.service.MovieCommentsMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieCommentsMapperImpl implements MovieCommentsMapper {

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public MovieCommentsMapperImpl(CommentRepository commentRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public MovieDTO returnMovieWithComments(Movie movie) {

        List<CommentDTO> allDtos = commentRepository.findByMovie(movie).stream()
                .map(this::toDto)
                .toList();

        Map<Long, List<CommentDTO>> repliesByParent = allDtos.stream()
                .filter(c -> c.getParentCommentId() != null)
                .collect(Collectors.groupingBy(CommentDTO::getParentCommentId));

        List<CommentDTO> roots = allDtos.stream()
                .filter(c -> c.getParentCommentId() == null)
                .sorted(Comparator.comparing(CommentDTO::getCommentId))
                .toList();

        List<CommentDTO> ordered = new ArrayList<>();
        for (CommentDTO root : roots) {
            appendDepthFirst(root, repliesByParent, ordered);
        }

        MovieDTO response = modelMapper.map(movie, MovieDTO.class);
        response.setComments(ordered);
        return response;
    }

    private CommentDTO toDto(Comment comment) {
        CommentDTO dto = modelMapper.map(comment, CommentDTO.class);
        dto.setUserName(comment.getUser().getUsername());
        dto.setParentCommentId(comment.getParentComment() == null ? null : comment.getParentComment().getCommentId());
        return dto;
    }

    private void appendDepthFirst( CommentDTO node, Map<Long, List<CommentDTO>> repliesByParent, List<CommentDTO> out) {
        out.add(node);

        List<CommentDTO> children = repliesByParent.get(node.getCommentId());
        if (children == null || children.isEmpty()) return;

        children.sort(Comparator.comparing(CommentDTO::getCommentId));
        for (CommentDTO child : children) {
            appendDepthFirst(child, repliesByParent, out);
        }
    }
}
