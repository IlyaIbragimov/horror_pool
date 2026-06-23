package com.social.horror_pool.util;

import com.social.horror_pool.exception.APIException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaginationUtilsTest {

    @Test
    void createPageable_WithValidValues_ReturnsPageable() {
        Pageable pageable = PaginationUtils.createPageable(0, 50, Sort.by("title").ascending());

        assertEquals(0, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
        assertEquals(Sort.by("title").ascending(), pageable.getSort());
    }

    @Test
    void createPageable_WithNegativePage_ThrowsApiException() {
        APIException exception = assertThrows(APIException.class,
                () -> PaginationUtils.createPageable(-1, 5));

        assertEquals("Page number must be 0 or greater", exception.getMessage());
    }

    @Test
    void createPageable_WithZeroPageSize_ThrowsApiException() {
        APIException exception = assertThrows(APIException.class,
                () -> PaginationUtils.createPageable(0, 0));

        assertEquals("Page size must be between 1 and 50", exception.getMessage());
    }

    @Test
    void createPageable_WithTooLargePageSize_ThrowsApiException() {
        APIException exception = assertThrows(APIException.class,
                () -> PaginationUtils.createPageable(0, 51));

        assertEquals("Page size must be between 1 and 50", exception.getMessage());
    }
}
