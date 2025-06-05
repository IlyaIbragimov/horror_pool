package com.social.horror_pool.controller;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.dto.WatchlistItemDTO;
import com.social.horror_pool.payload.WatchlistAllResponse;
import com.social.horror_pool.payload.WatchlistByIdResponse;
import com.social.horror_pool.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/horrorpool/user/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService){
        this.watchlistService = watchlistService;
    }

    @PostMapping("/create")
    public ResponseEntity<WatchlistDTO> createWatchlist(@Valid @RequestBody WatchlistDTO watchlistDTO){
        WatchlistDTO response = this.watchlistService.createWatchlist(watchlistDTO.getTitle());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<WatchlistAllResponse> getAllWatchlists(
            @RequestParam(name = "page", defaultValue = AppConstants.PAGE_NUMBER, required = false)  Integer pageNumber,
            @RequestParam(name = "size", defaultValue = AppConstants.PAGE_SIZE, required = false)  Integer pageSize,
            @RequestParam(name = "order", defaultValue = AppConstants.ORDER_TYPE, required = false) String order
    ){
        WatchlistAllResponse response = this.watchlistService.getAllWatchlists(pageNumber, pageSize, order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{watchlistId}/update")
    public ResponseEntity<WatchlistDTO> renameWatchlist(
           @Valid @RequestBody WatchlistDTO watchlistDTO,
            @PathVariable Long watchlistId
    ){
        WatchlistDTO response = this.watchlistService.updateWatchlist(watchlistId ,watchlistDTO.getTitle());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}/delete")
    public ResponseEntity<WatchlistDTO> deleteWatchlist(@PathVariable Long watchlistId){
        WatchlistDTO response = this.watchlistService.deleteWatchlist(watchlistId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{watchlistId}/add/{movieID}")
    public ResponseEntity<WatchlistDTO> addMovieToWatchlist(
            @PathVariable Long watchlistId,
            @PathVariable Long movieID
    ) {
        WatchlistDTO response = this.watchlistService.addMovieToWatchlist(watchlistId, movieID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}/remove/{watchlistItemId}")
    public ResponseEntity<WatchlistDTO> removeMovieFromWatchlist(
            @PathVariable Long watchlistId,
            @PathVariable Long watchlistItemId
    ) {
        WatchlistDTO response = this.watchlistService.removeMovieFromWatchlist(watchlistId, watchlistItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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

    @PutMapping("/{watchlistId}/toggle/{watchlistItemId}")
    public ResponseEntity<WatchlistItemDTO> toggleWatchlistItem(
            @PathVariable Long watchlistId,
            @PathVariable Long watchlistItemId
    ) {
        WatchlistItemDTO response = this.watchlistService.toggleWatchlistItemAsWatched(watchlistId, watchlistItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
