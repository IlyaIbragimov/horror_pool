package com.social.horror_pool.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RateWatchlistRequest {
    @Min(0)
    @Max(10)
    @NotNull
    private double rating;
}
