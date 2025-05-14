package com.social.horror_pool.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private String path;
}
