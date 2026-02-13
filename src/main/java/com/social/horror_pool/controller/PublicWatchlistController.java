package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.payload.WatchlistByIdResponse;
import com.social.horror_pool.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(
            summary = "Get a watchlist items by watchlist ID and return as them as page",
            description = "Retrieve movies of a watchlist by ID with optional filtering by watched status and pagination. Only accessible to user who created the watchlist if watchlist is private, in case if it is public available for all."
    )
    @GetMapping("/{watchlistId}")
    public ResponseEntity<WatchlistByIdResponse> getWatchlistById(
            @PathVariable Long watchlistId,
            @RequestParam(name = "watched", required = false) Boolean watched,
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        WatchlistByIdResponse response = this.watchlistService.getWatchlistById(watchlistId, watched, pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get watchlist followers",
            description = "Get usernames of users who added this watchlist."
    )
    @GetMapping("/{watchlistId}/followers")
    public ResponseEntity<List<String>> getWatchlistFollowers(@PathVariable Long watchlistId) {
        List<String> response = this.watchlistService.getWatchlistFollowers(watchlistId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
