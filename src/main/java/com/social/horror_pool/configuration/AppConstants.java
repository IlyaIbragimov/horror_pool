package com.social.horror_pool.configuration;

import java.util.Set;

public class AppConstants {

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "5";
    public static final String ORDER_TYPE = "asc";
    public static final String SORT_TYPE_MOVIE_DEFAULT = "title";

    public static final String TMDB_DEFAULT_LANGUAGE = "en-US";
    public static final int TMDB_HORROR_GENRE_ID = 27;
    public static final int TMDB_MIN_VOTE_COUNT = 100;
    public static final String TMDB_DEFAULT_SORT_BY = "popularity.desc";
    public static final Set<String> TMDB_SUPPORTED_SORT_BY = Set.of(
            "original_title.asc",
            "original_title.desc",
            "popularity.asc",
            "popularity.desc",
            "revenue.asc",
            "revenue.desc",
            "primary_release_date.asc",
            "primary_release_date.desc",
            "title.asc",
            "title.desc",
            "vote_average.asc",
            "vote_average.desc",
            "vote_count.asc",
            "vote_count.desc"
    );

}
