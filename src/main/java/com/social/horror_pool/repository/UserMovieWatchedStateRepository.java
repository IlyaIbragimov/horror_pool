package com.social.horror_pool.repository;

import com.social.horror_pool.model.UserMovieWatchedState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMovieWatchedStateRepository extends JpaRepository<UserMovieWatchedState, Long> {
    Optional<UserMovieWatchedState> findByUser_UserIdAndMovie_MovieId(Long userId, Long movieId);
    List<UserMovieWatchedState> findAllByUser_UserIdAndMovie_MovieIdIn(Long userId, List<Long> movieIds);
}