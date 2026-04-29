import type { Genre } from "./genre.types";

export type MovieSummaryDTO = {
  movieId: number;
  title: string;
  releaseDate: string | null;  
  posterPath: string | null;
  voteAverage: number | null;
};

export type MovieDetailDTO = MovieSummaryDTO & {
  overview: string | null;
  voteCount: number | null;
  originalLanguage: string | null;
  comments: Comment[];
};

export type WatchlistMovieDTO = {
  movieId: number;
  title: string;
  posterPath: string | null;
  overview: string | null;
};

export type AdminMovieDTO = MovieDetailDTO & {
  tmdbId: number | null;
  originalTitle?: string | null;
  description?: string | null;
  releaseYear: number | null;
  backdropPath: string | null;
  popularity: number | null;
  adult?: boolean | null;
  video?: boolean | null;
  genres?: Genre[];
};

export type MovieAllResponse = {
    movies: MovieSummaryDTO[];
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
  parentCommentId: number;
}

export type CommentNode = Comment & { replies: CommentNode[] };
