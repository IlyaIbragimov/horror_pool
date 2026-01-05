export type MovieDTO = {
  movieId: number;
  tmdbId: number | null;
  title: string;
  overview: string | null;
  releaseDate: string | null;  
  releaseYear: number | null;
  posterPath: string | null;
  backdropPath: string | null;
  voteAverage: number | null;
  voteCount: number | null;
  popularity: number | null;
  originalLanguage: string | null;
}

export type MovieAllResponse = {
    movies: MovieDTO[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}