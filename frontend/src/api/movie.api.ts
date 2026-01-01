import { http } from "./http";
import type { MovieAllResponse, MovieDTO } from "../types/movie.types";

export type MoviesQuery = {
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
};

export function fetchMovies(params: MoviesQuery = {}): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/all${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}

export function fetchMovieById(movieId: number): Promise<MovieDTO> {
  return http<MovieDTO>(`/public/movie/${movieId}`);
}