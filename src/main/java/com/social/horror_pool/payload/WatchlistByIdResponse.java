package com.social.horror_pool.payload;

import com.social.horror_pool.dto.WatchlistItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistByIdResponse {
    private String title;
    private List<WatchlistItemDTO> items;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
