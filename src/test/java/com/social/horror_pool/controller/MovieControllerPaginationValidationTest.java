package com.social.horror_pool.controller;

import com.social.horror_pool.service.CommentService;
import com.social.horror_pool.service.MovieService;
import com.social.horror_pool.security.UserDetailsServiceImpl;
import com.social.horror_pool.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerPaginationValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    void getAllMovies_WithNegativePage_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/horrorpool/public/movie/all")
                        .param("page", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Page number must be 0 or greater"));

        verifyNoInteractions(this.movieService);
    }

    @Test
    void getAllMovies_WithZeroPageSize_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/horrorpool/public/movie/all")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Page size must be at least 1"));

        verifyNoInteractions(this.movieService);
    }

    @Test
    void getAllMovies_WithTooLargePageSize_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(get("/horrorpool/public/movie/all")
                        .param("page", "0")
                        .param("size", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Page size must not exceed 50"));

        verifyNoInteractions(this.movieService);
    }
}