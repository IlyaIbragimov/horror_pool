export type MovieDTO = {
  movieId: number;
  tmdbId: number | null;
  title: string;
  originalTitle: string | null;
  description: string | null;
  overview: string | null;
  releaseDate: string | null;  
  releaseYear: number | null;
  posterPath: string | null;
  backdropPath: string | null;
  voteAverage: number | null;
  voteCount: number | null;
  popularity: number | null;
  originalLanguage: string | null;
  adult: boolean | null;
  video: boolean | null;
}

export type MovieAllResponse = {
    movies: MovieDTO[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}