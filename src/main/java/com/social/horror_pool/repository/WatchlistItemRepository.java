package com.social.horror_pool.repository;

import com.social.horror_pool.model.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
}
