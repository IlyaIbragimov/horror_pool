package com.social.horror_pool.repository;

import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Page<Watchlist> findAllByUser(User user, Pageable pageable);
}
