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
  comments: Comment[];
}

export type MovieAllResponse = {
    movies: MovieDTO[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}

export type MoviesQuery = {
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
};

export type SearchMovieQuery = {
  keyword?: string;
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
};

export type Comment = {
  commentId: number;
  commentContent: string;
  userName: string;
  date: string;
}