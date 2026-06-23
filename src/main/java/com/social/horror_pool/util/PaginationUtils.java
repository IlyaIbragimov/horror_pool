package com.social.horror_pool.util;

import com.social.horror_pool.configuration.AppConstants;
import com.social.horror_pool.exception.APIException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable createPageable(Integer pageNumber, Integer pageSize) {
        validate(pageNumber, pageSize);
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Pageable createPageable(Integer pageNumber, Integer pageSize, Sort sort) {
        validate(pageNumber, pageSize);
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private static void validate(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            throw new APIException("Page number is required");
        }

        if (pageNumber < 0) {
            throw new APIException("Page number must be 0 or greater");
        }

        if (pageSize == null) {
            throw new APIException("Page size is required");
        }

        if (pageSize < 1 || pageSize > AppConstants.MAX_PAGE_SIZE) {
            throw new APIException("Page size must be between 1 and " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}