package com.social.horror_pool.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private String path;
    private List<String> errors;
}
