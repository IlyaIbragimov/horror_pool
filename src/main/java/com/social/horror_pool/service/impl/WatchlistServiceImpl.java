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
import com.social.horror_pool.payload.WatchlistByIdResponse;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.service.WatchlistService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;

    private final WatchlistItemRepository watchlistItemRepository;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    private final ModelMapper modelMapper;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, WatchlistItemRepository watchlistItemRepository, UserRepository userRepository, MovieRepository movieRepository, ModelMapper modelMapper) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WatchlistDTO createWatchlist(String title, boolean isPublic) {
        User user = getCurrentUser();

        List<Watchlist> usersWatchlist = user.getWatchlist();

        if (!usersWatchlist.isEmpty()) {
            for (Watchlist watchlist : usersWatchlist) {
                if (watchlist.getTitle().equals(title)) throw new APIException("You already have a watchlist with this title");
            }
        }

        Watchlist watchlist = new Watchlist();

        watchlist.setTitle(title);
        watchlist.setPublic(isPublic);
        watchlist.setRating(0L);
        watchlist.setUser(user);
        this.watchlistRepository.save(watchlist);
        return this.modelMapper.map(watchlist, WatchlistDTO.class);

    }

    @Override
    public WatchlistAllResponse getAllUserWatchlists(Integer pageNumber, Integer pageSize, String order) {
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
    public WatchlistDTO addMovieToWatchlist(Long watchlistId, Long movieId) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        Movie movie = this.movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", movieId));

        boolean alreadyInWatchlist = watchlist.getWatchlistItems().stream()
                .anyMatch(item -> item.getMovie().getMovieId().equals(movieId));
        if (alreadyInWatchlist) {
            throw new APIException("Movie is already in the watchlist.");
        }

        WatchlistItem watchlistItem = new WatchlistItem();
        watchlistItem.setMovie(movie);
        watchlistItem.setWatched(false);
        watchlistItem.setWatchlist(watchlist);

        watchlist.getWatchlistItems().add(watchlistItem);

        this.watchlistRepository.save(watchlist);

        return getWatchlistDTO(watchlist);
    }



    @Override
    @Transactional
    public WatchlistDTO removeMovieFromWatchlist(Long watchlistId, Long watchlistItemId) {

        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        WatchlistItem watchlistItemToRemove = this.watchlistItemRepository.findById(watchlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("WatchlistItem", "id", watchlistItemId));

        if (!watchlistItemToRemove.getWatchlist().equals(watchlist)) {
            throw new APIException("This movie does not belong to the specified watchlist.");
        }

        watchlist.getWatchlistItems().remove(watchlistItemToRemove);

        this.watchlistItemRepository.delete(watchlistItemToRemove);
        this.watchlistRepository.save(watchlist);
        return getWatchlistDTO(watchlist);

    }

    @Override
    public WatchlistByIdResponse getWatchlistItemsByWatchlistId(Long watchlistId, Boolean watched, Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user) && !watchlist.isPublic()) {
            throw new APIException("You do not have permission to view this watchlist.");
        }

        List<WatchlistItem> watchlistItems = watchlist.getWatchlistItems();

        Stream<WatchlistItem> watchlistItemStream = watchlistItems.stream();

        if (Boolean.TRUE.equals(watched)) {
            watchlistItemStream = watchlistItemStream.filter(WatchlistItem::isWatched);
        }

        if (Boolean.FALSE.equals(watched)) {
            watchlistItemStream = watchlistItemStream.filter(watchlistItem -> !watchlistItem.isWatched());
        }

        Comparator<WatchlistItem> comparator = Comparator.comparing(watchlistItem -> watchlistItem.getMovie().getTitle().toLowerCase());

        if (!order.equals("asc")) {
            comparator = comparator.reversed();
        }

        watchlistItemStream = watchlistItemStream.sorted(comparator);

        watchlistItems = watchlistItemStream.toList();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<WatchlistItem> page = new PageImpl<>(watchlistItems, pageable, watchlistItems.size());

        List<WatchlistItemDTO> watchlistItemDTOS = page.getContent().stream()
                .map(item -> {
                    WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                    watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                    return watchlistItemDTO;
                }).toList();

        WatchlistByIdResponse watchlistByIdResponse = new WatchlistByIdResponse();
        watchlistByIdResponse.setTitle(watchlist.getTitle());
        watchlistByIdResponse.setItems(watchlistItemDTOS);
        watchlistByIdResponse.setPageNumber(page.getNumber());
        watchlistByIdResponse.setPageSize(page.getSize());
        watchlistByIdResponse.setTotalElements(page.getTotalElements());
        watchlistByIdResponse.setTotalPages(page.getTotalPages());
        watchlistByIdResponse.setLastPage(page.isLast());

        return watchlistByIdResponse;

    }

    @Override
    public WatchlistItemDTO toggleWatchlistItemAsWatched(Long watchlistId, Long watchlistItemId) {

        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        WatchlistItem watchlistItem = this.watchlistItemRepository.findByWatchlist_WatchlistIdAndWatchItemId(watchlistId, watchlistItemId)
                        .orElseThrow(() -> new APIException("Movie was not found in the watchlist"));


        watchlistItem.setWatched(!watchlistItem.isWatched());
        this.watchlistItemRepository.save(watchlistItem);

        WatchlistItemDTO response = this.modelMapper.map(watchlistItem, WatchlistItemDTO.class);
        response.setMovieDTO(this.modelMapper.map(watchlistItem.getMovie(), MovieDTO.class));
        return response;

    }

    @Override
    public WatchlistAllResponse getAllPublicWatchlists(Integer pageNumber, Integer pageSize, String order) {

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByIsPublicTrue(pageable);

        return generateWatchlistAllResponse(page,pageNumber,pageSize);
    }

    @Override
    @Transactional
    public WatchlistDTO rateWatchlist(Long watchlistId, double rating) {
        if (rating > 10 || rating < 0) {
            throw new APIException("Rate value must be in the range 0-10.");
        }

        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (watchlist.getUser().equals(user)) {
            throw new APIException("You can not rate your watchlist.");
        }

        if (watchlist.getRaters().contains(user)) {
            throw new APIException("You have already rated this watchlist.");
        }

        if (!watchlist.isPublic()) {
            throw new APIException("Private watchlist cannot be rated.");
        }

        watchlist.getRaters().add(user);
        user.getRatedWatchlists().add(watchlist);
        int newRateCount = watchlist.getRateCount() + 1;

        watchlist.setRating((watchlist.getRating() * watchlist.getRateCount() + rating) / newRateCount);
        watchlist.setRateCount(newRateCount);
        this.watchlistRepository.save(watchlist);
        this.userRepository.save(user);
        return this.modelMapper.map(watchlist, WatchlistDTO.class);
    }

    @Override
    public WatchlistAllResponse getRatedWatchlistsByUser(Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByRatersContaining(user, pageable);

        return generateWatchlistAllResponse(page,pageNumber,pageSize);
    }

    @Override
    public WatchlistDTO addWatchlistToUser(Long watchlistId) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        user.getAddedWatchlists().add(watchlist);
        watchlist.getFollowers().add(user);
        this.userRepository.save(user);
        this.watchlistRepository.save(watchlist);


        return this.modelMapper.map(watchlist, WatchlistDTO.class);
    }

    @Override
    public List<String> getWatchlistFollowers(Long watchlistId) {
        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        return watchlist.getFollowers().stream()
                .map(User::getUsername)
                .toList();
    }

    @Override
    public WatchlistAllResponse getFollowedWatchlists(Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByFollowersContaining(user, pageable);

        return generateWatchlistAllResponse(page, pageNumber, pageSize);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new APIException("Please, sign in"));
    }


    private WatchlistAllResponse generateWatchlistAllResponse(Page<Watchlist> page, Integer pageNumber, Integer pageSize) {
        List<Watchlist> watchlists = page.getContent();


        List<WatchlistDTO> watchlistDTOS = watchlists.stream()
                .map(this::getWatchlistDTO).toList();

        WatchlistAllResponse watchlistAllResponse = new WatchlistAllResponse();
        watchlistAllResponse.setPageNumber(pageNumber);
        watchlistAllResponse.setPageSize(pageSize);
        watchlistAllResponse.setWatchlistDTOS(watchlistDTOS);
        watchlistAllResponse.setLastPage(page.isLast());
        watchlistAllResponse.setTotalPages(page.getTotalPages());
        watchlistAllResponse.setTotalElements(page.getTotalElements());
        return watchlistAllResponse;

    }

    private WatchlistDTO getWatchlistDTO(Watchlist watchlist) {

        WatchlistDTO watchlistDTO = this.modelMapper.map(watchlist, WatchlistDTO.class);

        List<WatchlistItemDTO> watchlistItemDTOS = watchlist.getWatchlistItems().stream()
                .map(item -> {
                    WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                    watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                    return watchlistItemDTO;
                }).toList();

        watchlistDTO.setWatchlistItemDTOS(watchlistItemDTOS);
        return watchlistDTO;
    }
}
