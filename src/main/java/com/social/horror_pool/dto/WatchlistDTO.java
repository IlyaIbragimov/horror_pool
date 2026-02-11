package com.social.horror_pool.dto;

import com.social.horror_pool.model.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistDTO {

    private Long watchlistId;

    @NotBlank
    @Size(min = 3, max = 30, message = "Watchlist title must be 3-30 characters long" )
    private String title;

    private boolean isPublic;

    @Max(10)
    @Min(0)
    private double rating;

    private int rateCount;

    private List<WatchlistItemDTO> watchlistItemDTOS;
}
