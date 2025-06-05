package com.social.horror_pool.repository;

import com.social.horror_pool.model.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
    Optional<WatchlistItem> findByWatchlist_WatchlistIdAndWatchItemId(Long watchlistId, Long watchlistItemId);
}
