import type { Genre } from "./genre.types";

export type AdminMoviePayload = {
  title: string;
  originalTitle?: string | null;
  description?: string | null;
  overview?: string | null;
  releaseDate?: string | null;
  releaseYear?: number | null;
  posterPath?: string | null;
  backdropPath?: string | null;
  voteAverage?: number | null;
  voteCount?: number | null;
  popularity?: number | null;
  originalLanguage?: string | null;
  adult?: boolean | null;
  video?: boolean | null;
  genres?: Genre[];
};

export type AdminUserInfoResponse = {
  userId: number;
  username: string;
  email: string;
  roles: string[];
  enabled: boolean;
  locked: boolean;
};

export type AdminGenrePayload = {
  name: string;
  description: string;
  posterPath?: string | null;
};

export type AdminMovieFormState = {
  title: string;
  originalTitle: string;
  description: string;
  overview: string;
  releaseDate: string;
  releaseYear: string;
  posterPath: string;
  backdropPath: string;
  voteAverage: string;
  voteCount: string;
  popularity: string;
  originalLanguage: string;
  adult: string;
  video: string;
  genreIds: string;
};
