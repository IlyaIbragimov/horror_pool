package com.social.horror_pool.service;

import com.social.horror_pool.dto.MovieDTO;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import com.social.horror_pool.model.User;
import com.social.horror_pool.payload.CreateCommentRequest;
import com.social.horror_pool.repository.CommentRepository;
import com.social.horror_pool.repository.MovieRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.security.CustomUserDetails;
import com.social.horror_pool.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MovieCommentsMapper movieCommentsMapper;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user1;
    private User user2;
    private User adminUser;
    private Movie movie1;
    private Movie movie2;
    private MovieDTO movieDTO1;

    @BeforeEach
    public void setUp() {
        user1 = createUser(2L, "user1");
        user2 = createUser(3L, "user2");
        adminUser = createUser(1L, "admin");
        movie1 = createMovie(1L, "Alien");
        movie2 = createMovie(2L, "Hereditary");
        movieDTO1 = createMovieDTO(movie1);
        setAuthentication(user1);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void addCommentToMovie_Success_ReturnsMovieDto() {
        CreateCommentRequest request = createCommentRequest("Great movie!");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(movieDTO1);

        MovieDTO result = commentService.addCommentToMovie(1L, request);

        assertNotNull(result);
        assertEquals(movieDTO1, result);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        Comment saved = commentCaptor.getValue();
        assertEquals(user1, saved.getUser());
        assertEquals(movie1, saved.getMovie());
        assertEquals("Great movie!", saved.getCommentContent());
        assertNull(saved.getParentComment());
        assertNotNull(saved.getDate());

        verify(movieRepository).findById(1L);
        verify(movieCommentsMapper).returnMovieWithComments(movie1);
    }

    @Test
    public void addCommentToMovie_MovieNotFound_ThrowsResourceNotFoundException() {
        CreateCommentRequest request = createCommentRequest("Great movie!");

        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> commentService.addCommentToMovie(1L, request));
        assertEquals("Movie was not found with id : 1", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void replyToComment_Success_ReturnsMovieDto() {
        CreateCommentRequest request = createCommentRequest("Totally agree!");
        Comment parentComment = createComment(10L, "Original", user2, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(movieDTO1);

        MovieDTO result = commentService.replyToComment(1L, 10L, request);

        assertNotNull(result);
        assertEquals(movieDTO1, result);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        Comment saved = commentCaptor.getValue();
        assertEquals(user1, saved.getUser());
        assertEquals(movie1, saved.getMovie());
        assertEquals("Totally agree!", saved.getCommentContent());
        assertEquals(parentComment, saved.getParentComment());
        assertNotNull(saved.getDate());
    }

    @Test
    public void replyToComment_ParentBelongsToAnotherMovie_ThrowsApiException() {
        CreateCommentRequest request = createCommentRequest("Reply");
        Comment parentComment = createComment(10L, "Original", user2, movie2);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(10L)).thenReturn(Optional.of(parentComment));

        APIException exception = assertThrows(APIException.class, () -> commentService.replyToComment(1L, 10L, request));
        assertEquals("Comment does not belong to this movie", exception.getMessage());

        verify(commentRepository, never()).save(any());
    }

    @Test
    public void editComment_Success_ReturnsMovieDto() {
        CreateCommentRequest request = createCommentRequest("Updated content");
        Comment comment = createComment(20L, "Old", user1, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(20L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(movieDTO1);

        MovieDTO result = commentService.editComment(1L, 20L, request);

        assertNotNull(result);
        assertEquals(movieDTO1, result);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        Comment saved = commentCaptor.getValue();
        assertEquals("Updated content", saved.getCommentContent());
    }

    @Test
    public void editComment_NotAuthorisedUser_ThrowsApiException() {
        CreateCommentRequest request = createCommentRequest("Updated content");
        Comment comment = createComment(20L, "Old", user2, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(20L)).thenReturn(Optional.of(comment));

        APIException exception = assertThrows(APIException.class, () -> commentService.editComment(1L, 20L, request));
        assertEquals("You are not authorized to edit this comment", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void editComment_CommentBelongsToAnotherMovie_ThrowsApiException() {
        CreateCommentRequest request = createCommentRequest("Updated content");
        Comment comment = createComment(20L, "Old", user1, movie2);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(20L)).thenReturn(Optional.of(comment));

        APIException exception = assertThrows(APIException.class, () -> commentService.editComment(1L, 20L, request));
        assertEquals("Comment does not belong to the specified movie", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void deleteComment_Success_ReturnsMovieDto() {
        Comment comment = createComment(30L, "To delete", user1, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(movieDTO1);

        MovieDTO result = commentService.deleteComment(1L, 30L);

        assertNotNull(result);
        assertEquals(movieDTO1, result);
        verify(commentRepository).delete(comment);
    }

    @Test
    public void deleteComment_AdminUser_Success_ReturnsMovieDto() {
        setAuthentication(adminUser);
        Comment comment = createComment(30L, "To delete", user2, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));
        when(movieCommentsMapper.returnMovieWithComments(movie1)).thenReturn(movieDTO1);

        MovieDTO result = commentService.deleteComment(1L, 30L);

        assertNotNull(result);
        assertEquals(movieDTO1, result);
        verify(commentRepository).delete(comment);
    }

    @Test
    public void deleteComment_NotAuthorisedUser_ThrowsApiException() {
        setAuthentication(user2);
        Comment comment = createComment(30L, "To delete", user1, movie1);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        APIException exception = assertThrows(APIException.class, () -> commentService.deleteComment(1L, 30L));
        assertEquals("You are not authorized to delete this comment", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    public void deleteComment_CommentBelongsToAnotherMovie_ThrowsApiException() {
        Comment comment = createComment(30L, "To delete", user1, movie2);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie1));
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        APIException exception = assertThrows(APIException.class, () -> commentService.deleteComment(1L, 30L));
        assertEquals("Comment does not belong to the specified movie", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }

    private void setAuthentication(User user) {
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword("password" + id);
        user.setEmail(username + "@example.com");
        user.setComments(new ArrayList<>());
        user.setWatchlist(new ArrayList<>());
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
                movie.getTmdbId(),
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

    private Comment createComment(Long id, String content, User user, Movie movie) {
        Comment comment = new Comment();
        comment.setCommentId(id);
        comment.setCommentContent(content);
        comment.setUser(user);
        comment.setMovie(movie);
        return comment;
    }

    private CreateCommentRequest createCommentRequest(String content) {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setCommentContent(content);
        return request;
    }
}
