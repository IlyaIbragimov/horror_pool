import { http } from "./http";
import type {
  MovieAllResponse,
  MovieDTO,
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

export function fetchMovieById(movieId: number): Promise<MovieDTO> {
  return http<MovieDTO>(`/public/movie/${movieId}`);
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
): Promise<MovieDTO> {
  return http<MovieDTO>(`/movie/${movieId}/addComment`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function replyToComment(
  movieId: number,
  commentId: number,
  commentContent: string,
): Promise<MovieDTO> {
  return http<MovieDTO>(`/movie/${movieId}/comment/${commentId}/reply`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function editComment(
  movieId: number,
  commentId: number,
  commentContent: string,
): Promise<MovieDTO> {
    return http<MovieDTO>(`/movie/${movieId}/editComment/${commentId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ commentContent })
  });
}

export function deleteComment(
  movieId: number,
  commentId: number,
): Promise<MovieDTO> {
    return http<MovieDTO>(`/movie/${movieId}/deleteComment/${commentId}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" }
  });
}

