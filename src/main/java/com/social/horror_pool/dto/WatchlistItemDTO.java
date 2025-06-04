package com.social.horror_pool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItemDTO {

    private Long watchItemId;

    private MovieDTO movieDTO;

    private List<WatchlistItemDTO> watchlistItemDTOList;

    private boolean watched;

}
