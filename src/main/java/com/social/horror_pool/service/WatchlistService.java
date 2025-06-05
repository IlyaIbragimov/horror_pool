package com.social.horror_pool.service;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.payload.WatchlistByIdResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface WatchlistService {
    WatchlistDTO createWatchlist(String title);

    WatchlistAllResponse getAllWatchlists(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO updateWatchlist(Long watchlistId, @NotBlank @Size(min = 3, max = 30, message = "Watchlist title must be 3-30 characters long" ) String title);

    WatchlistDTO deleteWatchlist(Long watchlistId);

    WatchlistDTO addMovieToWatchlist(Long watchlistId, Long movieId);

    WatchlistDTO removeMovieFromWatchlist(Long watchlistId, Long watchlistItemId);

    WatchlistByIdResponse getWatchlistById(Long watchlistId, Boolean watched, Integer pageNumber, Integer pageSize, String order);

    WatchlistItemDTO toggleWatchlistItemAsWatched(Long watchlistId, Long watchlistItemId);
}
