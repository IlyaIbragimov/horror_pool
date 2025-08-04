package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.service.impl.WatchlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class WatchlistServiceImplTest {
    @Mock
    private WatchlistRepository watchlistRepository;
    @Mock
    private WatchlistItemRepository watchlistItemRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private WatchlistServiceImpl watchlistServiceImpl;

    private Watchlist watchlist1, watchlist2;

    private WatchlistDTO watchlistDTO1, watchlistDTO2;

    private Movie movie1, movie2;

    private User user1, user2;

    @BeforeEach
    public void setUp() {
        user1 = createUser(1L);
        user2 = createUser(2L);
        watchlist1 = createWatchlist(1L, "Favorite", user1);
        watchlist2 = createWatchlist(2L, "New", user2);
        watchlistDTO1 = createWatchlistDTO(watchlist1);
        watchlistDTO2 = createWatchlistDTO(watchlist2);
        movie1 = createMovie(1L, "Alien");
        movie2 = createMovie(2L, "Friday the 13th");
    }





    private Watchlist createWatchlist(Long watchlistId, String title, User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setWatchlistId(watchlistId);
        watchlist.setTitle(title);
        watchlist.setUser(user);
        watchlist.setWatchlistItems(new ArrayList<>());
        return watchlist;
    }

    private WatchlistDTO createWatchlistDTO(Watchlist watchlist) {
        WatchlistDTO watchlistDTO = new WatchlistDTO();
        watchlistDTO.setWatchlistId(watchlist.getWatchlistId());
        watchlistDTO.setTitle(watchlist.getTitle());
        watchlistDTO.setWatchlistItemDTOS(new ArrayList<>());
        return watchlistDTO;
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }


    private Movie createMovie(Long id, String title) {
        Movie movie = new Movie();
        movie.setMovieId(id);
        movie.setTitle(title);
        movie.setOriginalTitle(title);
        movie.setDescription("Description for " + title);
        movie.setOverview("Overview for " + title);
        movie.setReleaseDate(LocalDate.of(2000 + id.intValue(), 1, 1));
        movie.setReleaseYear(2000 + id.intValue());
        movie.setPosterPath("/" + title.toLowerCase() + "_poster.jpg");
        movie.setBackdropPath("/" + title.toLowerCase() + "_backdrop.jpg");
        movie.setVoteAverage(7.0 + id);
        movie.setVoteCount(1000 * id.intValue());
        movie.setPopularity(50.0 + id);
        movie.setOriginalLanguage("en");
        movie.setAdult(false);
        movie.setVideo(false);
        movie.setGenres(new ArrayList<>());
        movie.setWatchlistItems(new ArrayList<>());
        movie.setComments(new ArrayList<>());
        return movie;
    }

    private MovieDTO createMovieDTO(Movie movie) {
        return new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getDescription(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getReleaseYear(),
                movie.getPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.getVoteCount(),
                movie.getPopularity(),
                movie.getOriginalLanguage(),
                movie.getAdult(),
                movie.getVideo(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }


}
