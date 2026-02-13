package com.social.horror_pool.repository;

import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Page<Watchlist> findAllByUser(User user, Pageable pageable);
    Page<Watchlist> findAllByIsPublicTrue(Pageable pageable);
    Page<Watchlist> findAllByRatersContaining(User user, Pageable pageable);
    Page<Watchlist> findAllByFollowersContaining(User user, Pageable pageable);
}
