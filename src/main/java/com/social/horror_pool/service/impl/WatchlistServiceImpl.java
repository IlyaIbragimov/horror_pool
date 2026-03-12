package com.social.horror_pool.service.impl;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.*;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.payload.WatchlistItemsByWatchlistIdResponse;
import com.social.horror_pool.repository.*;
import com.social.horror_pool.service.WatchlistService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;

    private final WatchlistItemRepository watchlistItemRepository;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    private final UserMovieWatchedStateRepository userMovieWatchedStateRepository;

    private final ModelMapper modelMapper;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, WatchlistItemRepository watchlistItemRepository, UserRepository userRepository, MovieRepository movieRepository, UserMovieWatchedStateRepository userMovieWatchedStateRepository, ModelMapper modelMapper) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.userMovieWatchedStateRepository = userMovieWatchedStateRepository;
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
        return getWatchlistDTO(watchlist, Optional.of(user));
    }

    @Override
    public WatchlistAllResponse getAllUserWatchlists(Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByUser(user, pageable);

        return generateWatchlistAllResponse(page, pageNumber, pageSize, Optional.of(user));
    }

    @Override
    public WatchlistDTO updateWatchlist(Long watchlistId, String title) {

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        User user = getCurrentUser();
        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        watchlist.setTitle(title);
        this.watchlistRepository.save(watchlist);
        return getWatchlistDTO(watchlist, Optional.of(user));
    }

    @Override
    public WatchlistDTO deleteWatchlist(Long watchlistId) {

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        User user = getCurrentUser();
        if (!watchlist.getUser().equals(user)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        WatchlistDTO result = getWatchlistDTO(watchlist, Optional.of(user));

        this.watchlistRepository.delete(watchlist);
        return result;
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
        watchlistItem.setWatchlist(watchlist);

        watchlist.getWatchlistItems().add(watchlistItem);

        this.watchlistRepository.save(watchlist);

        return getWatchlistDTO(watchlist, Optional.of(user));
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
        return getWatchlistDTO(watchlist, Optional.of(user));
    }

    @Override
    public WatchlistItemsByWatchlistIdResponse getWatchlistItemsByWatchlistId(Long watchlistId, Boolean watched, Integer pageNumber, Integer pageSize, String order) {

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        Optional<User> currentUser = getCurrentUserOptional();
        if (!watchlist.isPublic()) {
            User user = currentUser.orElseThrow(() -> new APIException("Please, sign in"));
            if (!watchlist.getUser().equals(user)) {
                throw new APIException("You do not have permission to view this watchlist.");
            }
        }

        List<WatchlistItem> watchlistItems = watchlist.getWatchlistItems();

        Map<Long, Boolean> watchedByMovieIdTmp = Map.of();
        if (currentUser.isPresent() && !watchlistItems.isEmpty()) {
            List<Long> movieIds = watchlistItems.stream()
                    .map(item -> item.getMovie().getMovieId())
                    .distinct()
                    .toList();

            watchedByMovieIdTmp = userMovieWatchedStateRepository
                    .findAllByUser_UserIdAndMovie_MovieIdIn(currentUser.get().getUserId(), movieIds)
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(
                            state -> state.getMovie().getMovieId(),
                            UserMovieWatchedState::isWatched,
                            (left, right) -> left
                    ));
        }

        final Map<Long, Boolean> watchedByMovieId = watchedByMovieIdTmp;

        Stream<WatchlistItem> watchlistItemStream = watchlistItems.stream();

        if (Boolean.TRUE.equals(watched)) {
            watchlistItemStream = watchlistItemStream.filter(item -> watchedByMovieId.getOrDefault(item.getMovie().getMovieId(), false));
        }

        if (Boolean.FALSE.equals(watched)) {
            watchlistItemStream = watchlistItemStream.filter(item -> !watchedByMovieId.getOrDefault(item.getMovie().getMovieId(), false));
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
                    watchlistItemDTO.setWatched(watchedByMovieId.getOrDefault(item.getMovie().getMovieId(), false));
                    watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                    return watchlistItemDTO;
                }).toList();

        WatchlistItemsByWatchlistIdResponse watchlistItemsByWatchlistIdResponse = new WatchlistItemsByWatchlistIdResponse();
        watchlistItemsByWatchlistIdResponse.setTitle(watchlist.getTitle());
        watchlistItemsByWatchlistIdResponse.setItems(watchlistItemDTOS);
        watchlistItemsByWatchlistIdResponse.setPageNumber(page.getNumber());
        watchlistItemsByWatchlistIdResponse.setPageSize(page.getSize());
        watchlistItemsByWatchlistIdResponse.setTotalElements(page.getTotalElements());
        watchlistItemsByWatchlistIdResponse.setTotalPages(page.getTotalPages());
        watchlistItemsByWatchlistIdResponse.setLastPage(page.isLast());

        return watchlistItemsByWatchlistIdResponse;
    }

    @Override
    @Transactional
    public WatchlistItemDTO toggleWatchlistItemAsWatched(Long watchlistId, Long watchlistItemId) {

        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        WatchlistItem watchlistItem = this.watchlistItemRepository.findById(watchlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("WatchlistItem", "id", watchlistItemId));

        if (!watchlistItem.getWatchlist().getWatchlistId().equals(watchlistId)) {
            throw new APIException("This movie does not belong to the specified watchlist.");
        }

        boolean isOwner = watchlist.getUser().equals(user);
        boolean isFollower = watchlist.getFollowers().contains(user);
        if (!isOwner && (!watchlist.isPublic() || !isFollower)) {
            throw new APIException("You do not have permission to modify this watchlist.");
        }

        UserMovieWatchedState state = userMovieWatchedStateRepository
                .findByUser_UserIdAndMovie_MovieId(user.getUserId(), watchlistItem.getMovie().getMovieId())
                .orElseGet(() -> {
                    UserMovieWatchedState s = new UserMovieWatchedState();
                    s.setUser(user);
                    s.setMovie(watchlistItem.getMovie());
                    s.setWatched(false);
                    return s;
                });

        state.setWatched(!state.isWatched());
        userMovieWatchedStateRepository.save(state);

        WatchlistItemDTO response = modelMapper.map(watchlistItem, WatchlistItemDTO.class);
        response.setWatched(state.isWatched());
        response.setMovieDTO(modelMapper.map(watchlistItem.getMovie(), MovieDTO.class));
        return response;
    }

    @Override
    public WatchlistAllResponse getAllPublicWatchlists(Integer pageNumber, Integer pageSize, String order) {

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByIsPublicTrue(pageable);

        return generateWatchlistAllResponse(page, pageNumber, pageSize, getCurrentUserOptional());
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
        return getWatchlistDTO(watchlist, Optional.of(user));
    }

    @Override
    public WatchlistAllResponse getRatedWatchlistsByUser(Integer pageNumber, Integer pageSize, String order) {
        User user = getCurrentUser();

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by("title").ascending() : Sort.by("title").descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Watchlist> page = this.watchlistRepository.findAllByRatersContaining(user, pageable);

        return generateWatchlistAllResponse(page, pageNumber, pageSize, Optional.of(user));
    }

    @Override
    @Transactional
    public WatchlistDTO followWatchlist(Long watchlistId) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (watchlist.getUser().equals(user)) {
            throw new APIException("You cannot follow your own watchlist");
        }

        if (user.getAddedWatchlists().contains(watchlist)) {
            throw new APIException("You are already following this watchlist");
        }

        user.getAddedWatchlists().add(watchlist);
        watchlist.getFollowers().add(user);
        this.userRepository.save(user);
        this.watchlistRepository.save(watchlist);

        return getWatchlistDTO(watchlist, Optional.of(user));
    }

    @Override
    @Transactional
    public WatchlistDTO unfollowWatchlist(Long watchlistId) {
        User user = getCurrentUser();

        Watchlist watchlist = this.watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist", "id", watchlistId));

        if (!watchlist.getFollowers().contains(user)) {
            throw new APIException("You are not following this watchlist");
        }

        user.getAddedWatchlists().remove(watchlist);
        watchlist.getFollowers().remove(user);
        this.userRepository.save(user);
        this.watchlistRepository.save(watchlist);

        return getWatchlistDTO(watchlist, Optional.of(user));
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

        return generateWatchlistAllResponse(page, pageNumber, pageSize, Optional.of(user));
    }

    private Optional<User> getCurrentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        String username = auth.getName();
        if (username == null || username.equals("anonymousUser")) {
            return Optional.empty();
        }

        return userRepository.findByUsername(username);
    }

    private User getCurrentUser() {
        return getCurrentUserOptional()
                .orElseThrow(() -> new APIException("Please, sign in"));
    }


    private WatchlistAllResponse generateWatchlistAllResponse(Page<Watchlist> page, Integer pageNumber, Integer pageSize, Optional<User> currentUser) {
        List<Watchlist> watchlists = page.getContent();


        List<WatchlistDTO> watchlistDTOS = watchlists.stream()
                .map(watchlist -> getWatchlistDTO(watchlist, currentUser)).toList();

        WatchlistAllResponse watchlistAllResponse = new WatchlistAllResponse();
        watchlistAllResponse.setPageNumber(pageNumber);
        watchlistAllResponse.setPageSize(pageSize);
        watchlistAllResponse.setWatchlistDTOS(watchlistDTOS);
        watchlistAllResponse.setLastPage(page.isLast());
        watchlistAllResponse.setTotalPages(page.getTotalPages());
        watchlistAllResponse.setTotalElements(page.getTotalElements());
        return watchlistAllResponse;

    }

    private WatchlistDTO getWatchlistDTO(Watchlist watchlist, Optional<User> currentUser) {

        WatchlistDTO watchlistDTO = this.modelMapper.map(watchlist, WatchlistDTO.class);

        Map<Long, Boolean> watchedByMovieIdTmp = Map.of();
        if (currentUser.isPresent() && !watchlist.getWatchlistItems().isEmpty()) {
            List<Long> movieIds = watchlist.getWatchlistItems().stream()
                    .map(item -> item.getMovie().getMovieId())
                    .distinct()
                    .toList();

            watchedByMovieIdTmp = userMovieWatchedStateRepository
                    .findAllByUser_UserIdAndMovie_MovieIdIn(currentUser.get().getUserId(), movieIds)
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(
                            state -> state.getMovie().getMovieId(),
                            UserMovieWatchedState::isWatched,
                            (left, right) -> left
                    ));
        }

        final Map<Long, Boolean> watchedByMovieId = watchedByMovieIdTmp;

        List<WatchlistItemDTO> watchlistItemDTOS = watchlist.getWatchlistItems().stream()
                .map(item -> {
                    WatchlistItemDTO watchlistItemDTO = this.modelMapper.map(item, WatchlistItemDTO.class);
                    watchlistItemDTO.setWatched(watchedByMovieId.getOrDefault(item.getMovie().getMovieId(), false));
                    watchlistItemDTO.setMovieDTO(this.modelMapper.map(item.getMovie(), MovieDTO.class));
                    return watchlistItemDTO;
                }).toList();

        watchlistDTO.setWatchlistItemDTOS(watchlistItemDTOS);
        watchlistDTO.setFollowersCount(watchlist.getFollowers().size());
        boolean followedByMe = currentUser
                .map(u -> watchlist.getFollowers().contains(u))
                .orElse(false);

        watchlistDTO.setFollowedByMe(followedByMe);
        return watchlistDTO;
    }
}