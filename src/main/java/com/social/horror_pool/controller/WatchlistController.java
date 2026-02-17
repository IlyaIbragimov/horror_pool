package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Watchlist", description = "Endpoints for managing user watchlists and items")
@RestController
@RequestMapping("/horrorpool/user/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService){
        this.watchlistService = watchlistService;
    }

    @Operation(
            summary = "Create a new watchlist",
            description = "Create a new watchlist for the currently logged-in user."
    )
    @PostMapping("/create")
    public ResponseEntity<WatchlistDTO> createWatchlist(@Valid @RequestBody WatchlistDTO watchlistDTO){
        WatchlistDTO response = this.watchlistService.createWatchlist(watchlistDTO.getTitle(), watchlistDTO.isPublic());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all watchlists of the current user",
            description = "Retrieve a paginated list of all watchlists for the currently logged-in user."
    )
    @GetMapping("/allByUser")
    public ResponseEntity<WatchlistAllResponse> getAllUserWatchlists(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        WatchlistAllResponse response = this.watchlistService.getAllUserWatchlists(pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Rename a watchlist",
            description = "Update the title of a watchlist by its ID. Only accessible to user who created the watchlist."
    )
    @PutMapping("/{watchlistId}/update")
    public ResponseEntity<WatchlistDTO> renameWatchlist(
           @Valid @RequestBody WatchlistDTO watchlistDTO,
            @PathVariable Long watchlistId
    ){
        WatchlistDTO response = this.watchlistService.updateWatchlist(watchlistId ,watchlistDTO.getTitle());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a watchlist",
            description = "Delete a watchlist by its ID. Only accessible to user who created the watchlist."
    )
    @DeleteMapping("/{watchlistId}/delete")
    public ResponseEntity<WatchlistDTO> deleteWatchlist(@PathVariable Long watchlistId){
        WatchlistDTO response = this.watchlistService.deleteWatchlist(watchlistId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Add a movie to a watchlist",
            description = "Add a movie to a watchlist by watchlist ID and movie ID. Only accessible to user who created the watchlist."
    )
    @PostMapping("/{watchlistId}/add/{movieId}")
    public ResponseEntity<WatchlistDTO> addMovieToWatchlist(
            @PathVariable Long watchlistId,
            @PathVariable Long movieId
    ) {
        WatchlistDTO response = this.watchlistService.addMovieToWatchlist(watchlistId, movieId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Remove a movie from a watchlist",
            description = "Remove a movie from a watchlist by watchlist ID and watchlist item ID. Only accessible to user who created the watchlist."
    )
    @DeleteMapping("/{watchlistId}/remove/{watchlistItemId}")
    public ResponseEntity<WatchlistDTO> removeMovieFromWatchlist(
            @PathVariable Long watchlistId,
            @PathVariable Long watchlistItemId
    ) {
        WatchlistDTO response = this.watchlistService.removeMovieFromWatchlist(watchlistId, watchlistItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{watchlistId}/rate")
    public ResponseEntity<WatchlistDTO> rateWatchlist(
            @Valid @RequestBody WatchlistDTO watchlistDTO,
            @PathVariable Long watchlistId
    ) {
        WatchlistDTO response = this.watchlistService.rateWatchlist(watchlistId, watchlistDTO.getRating());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Toggle the watched status of a movie in a watchlist",
            description = "Mark a movie in a watchlist as watched or unwatched. Only accessible to user who created the watchlist."
    )
    @PutMapping("/{watchlistId}/toggle/{watchlistItemId}")
    public ResponseEntity<WatchlistItemDTO> toggleWatchlistItem(
            @PathVariable Long watchlistId,
            @PathVariable Long watchlistItemId
    ) {
        WatchlistItemDTO response = this.watchlistService.toggleWatchlistItemAsWatched(watchlistId, watchlistItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get rated watchlists by user",
            description = "Get all watchlists rated by user. Only accessible to logged in user who rated those watchlists."
    )
    @GetMapping("/rated")
    public ResponseEntity<WatchlistAllResponse> getRatedWatchlistsByUser(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        WatchlistAllResponse response = this.watchlistService.getRatedWatchlistsByUser(pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Add watchlist to logged user's followed watchlists",
            description = "Add watchlist to logged user's followed watchlists. Only accessible to the logged-in user."
    )
    @PutMapping("/{watchlistId}/add")
    public ResponseEntity<WatchlistDTO> addWatchlistToUser(
            @PathVariable Long watchlistId
    ) {
        WatchlistDTO response = this.watchlistService.addWatchlistToUser(watchlistId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get followed watchlists",
            description = "Get all watchlists followed by currently logged-in user."
    )
    @GetMapping("/followed")
    public ResponseEntity<WatchlistAllResponse> getFollowedWatchlists(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ) {
        WatchlistAllResponse response = this.watchlistService.getFollowedWatchlists(pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
