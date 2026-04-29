import { http } from "./http";
import type {
  AdminMovieDTO,
  MovieAllResponse,
  MoviesQuery,
  SearchMovieQuery,
} from "../types/movie.types";

export function fetchMovies(
  params: MoviesQuery = {},
): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/all${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}

export function fetchMovieById(movieId: number): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>(`/public/movie/${movieId}`);
}

export function searchMovie(
  params: SearchMovieQuery = {},
): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.keyword !== undefined) search.set("keyword", params.keyword);
  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/search${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}

export function addCommentToMovie(
  movieId: number,
  commentContent: string,
): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>(`/movie/${movieId}/addComment`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function replyToComment(
  movieId: number,
  commentId: number,
  commentContent: string,
): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>(`/movie/${movieId}/comment/${commentId}/reply`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function editComment(
  movieId: number,
  commentId: number,
  commentContent: string,
): Promise<AdminMovieDTO> {
    return http<AdminMovieDTO>(`/movie/${movieId}/editComment/${commentId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function deleteComment(
  movieId: number,
  commentId: number,
): Promise<AdminMovieDTO> {
    return http<AdminMovieDTO>(`/movie/${movieId}/deleteComment/${commentId}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" }
  });
}

export function fetchMoviesByGenre(
  genreId: number,
  params: MoviesQuery = {},
): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/genre/${genreId}${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}
