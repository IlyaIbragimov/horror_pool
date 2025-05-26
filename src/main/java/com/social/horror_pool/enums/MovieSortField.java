package com.social.horror_pool.enums;

import java.util.Arrays;

public enum MovieSortField {
    TITLE("title"),
    RELEASE_DATE("releaseDate"),
    POPULARITY("popularity"),
    VOTE_AVG("voteAverage");

    private final String field;


    MovieSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static boolean isValidField(String input) {

        return Arrays.stream(values()).anyMatch(v -> v.getField().equalsIgnoreCase(input));

    }
}
