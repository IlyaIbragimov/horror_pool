package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.service.WatchlistService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        User user = getCurrentUser();

        Watchlist watchlist = new Watchlist();

        watchlist.setTitle(title);
        watchlist.setUser(user);
        this.watchlistRepository.save(watchlist);
        return this.modelMapper.map(watchlist, WatchlistDTO.class);

    }

    @Override
    public WatchlistAllResponse getAllWatchlists(Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByUser(user, pageable);

        return generateWatchlistAllResponse(page,pageNumber,pageSize);

    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new APIException("Please sign in to create a watchlist"));
    }

    private WatchlistAllResponse generateWatchlistAllResponse(Page<Watchlist> page, Integer pageNumber, Integer pageSize) {
        List<Watchlist> watchlists = page.getContent();
        List<WatchlistDTO> watchlistDTOS = watchlists.stream()
                .map(watchlist -> this.modelMapper.map(watchlist, WatchlistDTO.class)).toList();
        WatchlistAllResponse watchlistAllResponse = new WatchlistAllResponse();
        watchlistAllResponse.setPageNumber(pageNumber);
        watchlistAllResponse.setPageSize(pageSize);
        watchlistAllResponse.setWatchlistDTOS(watchlistDTOS);
        watchlistAllResponse.setLastPage(page.isLast());
        watchlistAllResponse.setTotalPages(page.getTotalPages());
        watchlistAllResponse.setTotalElements(page.getTotalElements());
        return watchlistAllResponse;

    }
}
