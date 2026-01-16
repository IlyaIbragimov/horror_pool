import { http } from "./http";
import type { MovieAllResponse, MovieDTO, MoviesQuery, SearchMovieQuery } from "../types/movie.types";

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

export function searchMovie(params: SearchMovieQuery = {}): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.keyword !== undefined) search.set("keyword", params.keyword)
  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/search${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}