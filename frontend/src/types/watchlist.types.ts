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
  sort?: string;
  order?: string;
};