package com.social.horror_pool.repository;

import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMovie(Movie movie);
}
