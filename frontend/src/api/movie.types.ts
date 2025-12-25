export type MovieDTO = {
    movieId?: number;
    tmdbId?: number;
    title?: string;
    originalTitle?: string;
    description?: string;
    overview?: string;
    releaseDate?: string;
    releaseYear?: number;
    posterPath?: string;
    backdropPath?: string;
    voteAverage?: number;
    voteCount?: number;
    popularity?: number;
    originalLanguage?: string;
    adult?: boolean;
    video?: boolean;
}

export type MovieAllResponse = {
    movies: MovieDTO[];
    pageNumber?: number;
    pageSize?: number;
    totalElements?: number;
    totalPages?: number;
    lastPage?: boolean
}