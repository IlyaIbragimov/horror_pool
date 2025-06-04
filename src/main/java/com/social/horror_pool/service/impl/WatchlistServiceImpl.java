package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.model.WatchlistItem;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.service.WatchlistService;
import jakarta.transaction.Transactional;
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

        List<Watchlist> usersWatchlist = user.getWatchlist();

        if (!usersWatchlist.isEmpty()) {
            for (Watchlist watchlist : usersWatchlist) {
                if (watchlist.getTitle().equals(title)) throw new APIException("You already have a watchlist with this title");
            }
        }

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

    @Override
    public WatchlistDTO updateWatchlist(Long watchlistId, String title) {

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(getCurrentUser())) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        watchlist.setTitle(title);
        this.watchlistRepository.save(watchlist);
        return this.modelMapper.map(watchlist, WatchlistDTO.class);

    }

    @Override
    public WatchlistDTO deleteWatchlist(Long watchlistId) {

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(getCurrentUser())) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        this.watchlistRepository.delete(watchlist);
        return this.modelMapper.map(watchlist, WatchlistDTO.class);
    }

    @Override
    @Transactional
    public WatchlistDTO addMovieToWatchlist(Long watchlistId, Long movieID) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        Movie movie = this.movieRepository.findById(movieID)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieID));

        boolean alreadyInWatchlist = watchlist.getWatchlistItems().stream()
                .anyMatch(item -> item.getMovie().getMovieId().equals(movieID));
        if (alreadyInWatchlist) {
            throw new APIException("Movie is already in the watchlist.");
        }

        WatchlistItem watchlistItem = new WatchlistItem();
        watchlistItem.setMovie(movie);
        watchlistItem.setWatched(false);
        watchlistItem.setWatchlist(watchlist);

        watchlist.getWatchlistItems().add(watchlistItem);

        this.watchlistRepository.save(watchlist);


        WatchlistDTO response = this.modelMapper.map(watchlist, WatchlistDTO.class);

        List<WatchlistItemDTO> watchlistItemDTOS = watchlist.getWatchlistItems().stream()
                        .map(item -> {
                            WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                            watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                            return watchlistItemDTO;
                        }).toList();

        response.setWatchlistItemDTOS(watchlistItemDTOS);
        return response;
    }

    @Override
    public WatchlistDTO removeMovieFromWatchlist(Long watchlistId, Long watchlistItemId) {

        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        WatchlistItem watchlistItemToRemove = this.watchlistItemRepository.findById(watchlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("WatchlistItem", "id", watchlistItemId));

        watchlist.getWatchlistItems().remove(watchlistItemToRemove);

        this.watchlistRepository.save(watchlist);

        WatchlistDTO response = this.modelMapper.map(watchlist, WatchlistDTO.class);

        List<WatchlistItemDTO> watchlistItemDTOS = watchlist.getWatchlistItems().stream()
                .map(item -> {
                    WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                    watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                    return watchlistItemDTO;
                }).toList();

        response.setWatchlistItemDTOS(watchlistItemDTOS);
        return response;

    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new APIException("Please, register sign in to create a watchlist"));
    }


    private WatchlistAllResponse generateWatchlistAllResponse(Page<Watchlist> page, Integer pageNumber, Integer pageSize) {
        List<Watchlist> watchlists = page.getContent();


        List<WatchlistDTO> watchlistDTOS = watchlists.stream()
                .map(watchlist -> {
                    WatchlistDTO watchlistDTO = this.modelMapper.map(watchlist, WatchlistDTO.class);

                    List<WatchlistItemDTO> watchlistItemDTOS = watchlist.getWatchlistItems().stream()
                            .map(item -> {
                                WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                                watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                                return watchlistItemDTO;
                            }).toList();

                    watchlistDTO.setWatchlistItemDTOS(watchlistItemDTOS);

                    return watchlistDTO;
                }).toList();


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
