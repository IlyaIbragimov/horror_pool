package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.service.WatchlistService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;

    private WatchlistItemRepository watchlistItemRepository;

    private final UserRepository userRepository;

    private MovieRepository movieRepository;

    private final ModelMapper modelMapper;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, WatchlistItemRepository watchlistItemRepository, UserRepository userRepository, MovieRepository movieRepository, ModelMapper modelMapper) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WatchlistDTO createWatchlist(String title) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new APIException("Please sign in to create a watchlist"));

        Watchlist watchlist = new Watchlist();

        watchlist.setTitle(title);
        watchlist.setUser(user);
        watchlistRepository.save(watchlist);
        return modelMapper.map(watchlist, WatchlistDTO.class);

    }
}
