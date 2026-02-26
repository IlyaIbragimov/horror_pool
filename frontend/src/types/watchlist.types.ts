import type { MovieDTO } from "../types/movie.types";

export type WatchlistItemDTO = {
  watchItemId: number;
  movieDTO: MovieDTO;
  watchlistId: number;
  watched: boolean | null;
}

export type WatchlistDTO = {
  watchlistId: number;
  title: string;
  isPublic: boolean;
  rating: number | null;
  rateCount: number | null;
  watchlistItemDTOS: WatchlistItemDTO[];
  followersCount: number;
  followedByMe: boolean
}

export type WatchlistAllResponse = {
    watchlistDTOS: WatchlistDTO[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}

export type WatchlistQuery = {
  page?: number;
  size?: number;
  order?: string;
};

export type WatchlistItemsByWatchlistIdResponse = {
    title: string;
    items: WatchlistItemDTO[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}

export type WatchlistItemsByWatchlistIdQuery = {
  watched?: boolean;
  page?: number;
  size?: number;
  order?: string;
};