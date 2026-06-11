import type { WatchlistMovieDTO } from "../types/movie.types";
import type { PaginatedResponse } from "./pagination.types";

export type WatchlistItemDTO = {
  watchItemId: number;
  movieDTO: WatchlistMovieDTO;
  watchlistId: number;
  watched: boolean;
};

export type WatchlistDTO = {
  watchlistId: number;
  title: string;
  isPublic: boolean;
  rating: number | null;
  rateCount: number | null;
  watchlistItemDTOS: WatchlistItemDTO[];
  followersCount: number;
  followedByMe: boolean;
  ownedByMe: boolean;
};

export type WatchlistAllResponse = PaginatedResponse & {
  watchlistDTOS: WatchlistDTO[];
};

export type WatchlistQuery = {
  page?: number;
  size?: number;
  order?: string;
};

export type WatchlistItemsByWatchlistIdResponse = PaginatedResponse & {
  title: string;
  items: WatchlistItemDTO[];
};

export type WatchlistItemsByWatchlistIdQuery = {
  watched?: boolean;
  page?: number;
  size?: number;
  order?: string;
};
