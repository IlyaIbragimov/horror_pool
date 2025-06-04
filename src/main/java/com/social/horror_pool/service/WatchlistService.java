package com.social.horror_pool.service;

import com.social.horror_pool.dto.WatchlistDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface WatchlistService {
    WatchlistDTO createWatchlist(String title);

    List<WatchlistDTO> getAllWatchlists();
}
