import type { Genre } from "./genre.types";

export type AdminMoviePayload = {
  title: string;
  originalTitle?: string | null;
  overview?: string | null;
  releaseDate?: string | null;
  releaseYear?: number | null;
  posterPath?: string | null;
  voteAverage?: number | null;
  voteCount?: number | null;
  popularity?: number | null;
  originalLanguage?: string | null;
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

export type TmdbDiscoverRequest = {
  pages?: number | null;
  sortBy?: string | null;
  releaseDateFrom?: string | null;
  releaseDateTo?: string | null;
  minVoteAverage?: number | null;
};

export type TmdbDiscoverFormState = {
  pages: string;
  sortBy: string;
  releaseDateFrom: string;
  releaseDateTo: string;
  minVoteAverage: string;
};

export type BulkImportResultResponse = {
  imported: number;
  skipped: number;
  failed: number;
  errors: string[];
};

export type AdminMovieFormState = {
  title: string;
  originalTitle: string;
  overview: string;
  releaseDate: string;
  releaseYear: string;
  posterPath: string;
  voteAverage: string;
  voteCount: string;
  popularity: string;
  originalLanguage: string;
  genreIds: string;
};
