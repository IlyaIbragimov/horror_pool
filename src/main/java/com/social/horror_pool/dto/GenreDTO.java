package com.social.horror_pool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDTO {

    private Long genreId;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 30 , message = "Name must be 3-30 characters")
    private String name;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 1000 , message = "Description cannot be longer than 1000 characters")
    private String description;
}
