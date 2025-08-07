package com.social.horror_pool.service;

import com.social.horror_pool.configuration.RoleName;
import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.Role;
import com.social.horror_pool.model.User;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.repository.WatchlistItemRepository;
import com.social.horror_pool.repository.WatchlistRepository;
import com.social.horror_pool.security.CustomUserDetails;
import com.social.horror_pool.service.impl.WatchlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        CustomUserDetails customUserDetails = new CustomUserDetails(user1);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void createWatchlist_Success_ReturnWatchlistDTO() {
        Watchlist newWatchlist = createWatchlist(3L, "Test", user1);
        WatchlistDTO newWatchlistDTO = createWatchlistDTO(newWatchlist);

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user1));
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(newWatchlist);
        when(modelMapper.map(any(Watchlist.class), eq(WatchlistDTO.class))).thenReturn(newWatchlistDTO);

        WatchlistDTO result = watchlistServiceImpl.createWatchlist("Test");

        assertNotNull(result);
        assertEquals(result, newWatchlistDTO);
        verify(userRepository).findByUsername("username1");
    }
    @Test
    public void createWatchlist_WatchlistAlreadyExists_ReturnAPIException() {
        user1.setWatchlist(Collections.singletonList(watchlist1));

        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user1));

        APIException exception = assertThrows(APIException.class, () -> watchlistServiceImpl.createWatchlist("Favorite"));
        assertEquals("You already have a watchlist with this title", exception.getMessage());
    }

    @Test
    public void createWatchlist_UserIsNotLoggedIn_ReturnAPIException() {
        when(userRepository.findByUsername("username1")).thenReturn(Optional.empty());

        APIException exception = assertThrows(APIException.class, () -> watchlistServiceImpl.createWatchlist("Favorite"));
        assertEquals("Please, register sign in to create a watchlist", exception.getMessage());
    }

    @Test
    public void updateWatchlist_Success_ReturnWatchlistDTO() {
        when(watchlistRepository.findById(1L)).thenReturn(Optional.of(watchlist1));
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user1));
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(watchlist1);

        WatchlistDTO updatedWatchlistDTO = createWatchlistDTO(watchlist1);
        updatedWatchlistDTO.setTitle("Updated");

        when(modelMapper.map(watchlist1, WatchlistDTO.class)).thenReturn(updatedWatchlistDTO);

        WatchlistDTO result = watchlistServiceImpl.updateWatchlist(1L, "Updated");

        assertNotNull(result);
        assertEquals(updatedWatchlistDTO, result);
        verify(userRepository).findByUsername("username1");
        assertEquals("Updated", result.getTitle());
    }
    @Test
    public void updateWatchlist_NonExistingWatchlistId_ReturnResourceNotFoundException() {
        when(watchlistRepository.findById(4L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> watchlistServiceImpl.updateWatchlist(4L, "Updated"));

        assertEquals("Watchlist was not found with id : 4", exception.getMessage());
    }

    @Test
    public void updateWatchlist_NotAuthorisedUser_ReturnAPIException() {
        when(watchlistRepository.findById(1L)).thenReturn(Optional.of(watchlist1));
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user2));

        APIException exception = assertThrows(APIException.class, () -> watchlistServiceImpl.updateWatchlist(1L, "Updated"));

        assertEquals("You do not have permission to modify this watchlist.", exception.getMessage());
    }

    @Test
    public void deleteWatchlist_Success_ReturnWatchlistDTO() {
        when(watchlistRepository.findById(1L)).thenReturn(Optional.of(watchlist1));
        when(userRepository.findByUsername("username1")).thenReturn(Optional.of(user1));
        doNothing().when(watchlistRepository).delete(watchlist1);
        when(modelMapper.map(watchlist1, WatchlistDTO.class)).thenReturn(watchlistDTO1);

        WatchlistDTO result = watchlistServiceImpl.deleteWatchlist(1L);

        assertNotNull(result);
        assertEquals(watchlistDTO1, result);
        verify(userRepository, times(1)).findByUsername("username1");
        verify(modelMapper, times(1)).map(watchlist1, WatchlistDTO.class);
        verify(watchlistRepository, times(1)).delete(watchlist1);
    }

    @Test
    public void deleteWatchlist_NonExistingWatchlistId_ReturnResourceNotFoundException() {
        when(watchlistRepository.findById(4L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> watchlistServiceImpl.deleteWatchlist(4L));
        assertEquals("Watchlist was not found with id : 4", exception.getMessage());
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
        user.setPassword("password" + userId);
        user.setUsername("username" + userId);
        user.setEmail("email" + userId);
        user.setWatchlist(new ArrayList<>());
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName(RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
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
