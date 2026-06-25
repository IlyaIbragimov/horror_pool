package com.social.horror_pool.controller;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.security.UserDetailsServiceImpl;
import com.social.horror_pool.security.jwt.JwtTokenProvider;
import com.social.horror_pool.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WatchlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WatchlistControllerRateValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WatchlistService watchlistService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    void rateWatchlist_WithMissingRating_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(put("/horrorpool/user/watchlist/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Rating is required"));

        verifyNoInteractions(this.watchlistService);
    }

    @Test
    void rateWatchlist_WithRatingBelowMinimum_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(put("/horrorpool/user/watchlist/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":-0.1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Rating must be at least 0"));

        verifyNoInteractions(this.watchlistService);
    }

    @Test
    void rateWatchlist_WithRatingAboveMaximum_ReturnsBadRequest() throws Exception {
        this.mockMvc.perform(put("/horrorpool/user/watchlist/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":10.1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Rating must not exceed 10"));

        verifyNoInteractions(this.watchlistService);
    }

    @Test
    void rateWatchlist_WithValidRating_CallsService() throws Exception {
        WatchlistDTO response = new WatchlistDTO();
        response.setWatchlistId(1L);
        response.setRating(8.5);

        when(this.watchlistService.rateWatchlist(1L, 8.5)).thenReturn(response);

        this.mockMvc.perform(put("/horrorpool/user/watchlist/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":8.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watchlistId").value(1))
                .andExpect(jsonPath("$.rating").value(8.5));

        verify(this.watchlistService).rateWatchlist(1L, 8.5);
    }
}