package com.social.horror_pool.payload;

import com.social.horror_pool.dto.WatchlistDTO;
import lombok.Data;

import java.util.List;

@Data
public class WatchlistAllResponse {
    private List<WatchlistDTO> watchlistDTOS;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
