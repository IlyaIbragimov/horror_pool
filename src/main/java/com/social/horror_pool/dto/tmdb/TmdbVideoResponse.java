package com.social.horror_pool.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmdbVideoResponse {
    private List<TmdbVideoDTO> results = new ArrayList<>();
}