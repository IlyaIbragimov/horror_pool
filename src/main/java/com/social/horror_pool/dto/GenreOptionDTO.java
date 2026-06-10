package com.social.horror_pool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreOptionDTO {
    private Long genreId;
    private String name;
}