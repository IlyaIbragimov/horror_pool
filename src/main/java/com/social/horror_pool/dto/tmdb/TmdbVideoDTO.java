package com.social.horror_pool.dto.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbVideoDTO {
    private String key;
    private String site;
    private String type;
    private Boolean official;
}