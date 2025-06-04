package com.social.horror_pool.service;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.payload.WatchlistAllResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface WatchlistService {
    WatchlistDTO createWatchlist(String title);

    WatchlistAllResponse getAllWatchlists(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO updateWatchlist(Long watchlistId, @NotBlank @Size(min = 3, max = 30, message = "Watchlist title must be 3-30 characters long" ) String title);

    WatchlistDTO deleteWatchlist(Long watchlistId);
}
