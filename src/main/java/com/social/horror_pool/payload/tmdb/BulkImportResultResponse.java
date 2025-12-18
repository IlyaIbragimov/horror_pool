package com.social.horror_pool.payload.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResultResponse {
    private int imported;
    private int skipped;
    private int failed;
    private List<String> errors = new ArrayList<>();
}
