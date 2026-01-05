import { http } from "./http";
import type { GenreAllResponse, Genre } from "../types/genre.types";

export type GenersQuery = {
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
};

export function fetchGenres(params: GenersQuery = {}): Promise<GenreAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/genre/all${qs ? `?${qs}` : ""}`;

  return http<GenreAllResponse>(url);
}

export function fetchMovieById(movieId: number): Promise<Genre> {
  return http<Genre>(`/public/movie/${movieId}`);
}