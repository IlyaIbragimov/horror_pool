package com.social.horror_pool.service;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.payload.WatchlistItemsByWatchlistIdResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface WatchlistService {
    WatchlistDTO createWatchlist(String title, boolean isPublic);

    WatchlistAllResponse getAllUserWatchlists(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO updateWatchlist(Long watchlistId, @NotBlank @Size(min = 3, max = 30, message = "Watchlist title must be 3-30 characters long" ) String title);

    WatchlistDTO deleteWatchlist(Long watchlistId);

    WatchlistDTO addMovieToWatchlist(Long watchlistId, Long movieId);

    WatchlistDTO removeMovieFromWatchlist(Long watchlistId, Long watchlistItemId);

    WatchlistItemsByWatchlistIdResponse getWatchlistItemsByWatchlistId(Long watchlistId, Boolean watched, Integer pageNumber, Integer pageSize, String order);

    WatchlistItemDTO toggleWatchlistItemAsWatched(Long watchlistId, Long watchlistItemId);

    WatchlistAllResponse getAllPublicWatchlists(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO rateWatchlist(Long watchlistId, double rating);

    WatchlistAllResponse getRatedWatchlistsByUser(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO followWatchlist(Long watchlistId);

    List<String> getWatchlistFollowers(Long watchlistId);

    WatchlistAllResponse getFollowedWatchlists(Integer pageNumber, Integer pageSize, String order);

    WatchlistDTO unfollowWatchlist(Long watchlistId);
}
