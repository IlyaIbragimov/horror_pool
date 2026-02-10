package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Watchlist", description = "Endpoints for managing public watchlists")
@RestController
@RequestMapping("/horrorpool/public/watchlist")
public class PublicWatchlistController {

    private final WatchlistService watchlistService;

    public PublicWatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @Operation(
            summary = "Get all public watchlists",
            description = "Retrieve a paginated list of all public watchlists. Availble"
    )
    @GetMapping("/allPublic")
    public ResponseEntity<WatchlistAllResponse> getAllPublicWatchlists(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        WatchlistAllResponse response = this.watchlistService.getAllPublicWatchlists(pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
