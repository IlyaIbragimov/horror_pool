package com.social.horror_pool.controller;

import com.social.horror_pool.dto.WatchlistDTO;
import com.social.horror_pool.model.Watchlist;
import com.social.horror_pool.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/horrorpool")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService){
        this.watchlistService = watchlistService;
    }

    @PostMapping("/user/watchlist/create")
    public ResponseEntity<WatchlistDTO> createWatchlist(@RequestBody WatchlistDTO watchlistDTO){
        WatchlistDTO response = this.watchlistService.createWatchlist(watchlistDTO.getTitle());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }




}
