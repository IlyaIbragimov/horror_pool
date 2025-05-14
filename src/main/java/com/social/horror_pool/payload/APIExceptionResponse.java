package com.social.horror_pool.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private LocalDate timestamp;
}
