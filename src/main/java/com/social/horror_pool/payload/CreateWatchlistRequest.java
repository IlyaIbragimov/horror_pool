package com.social.horror_pool.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWatchlistRequest {

    @NotBlank
    @Size(min = 3, max = 30, message = "Watchlist title must be 3-30 characters long" )
    private String title;

    private boolean isPublic;
}
