package com.social.horror_pool.controller;

import com.social.horror_pool.service.WatchlistService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horrorpool")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService){
        this.watchlistService = watchlistService;
    }

    

}
