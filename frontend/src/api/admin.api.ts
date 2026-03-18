import { http } from "./http";
import type {
  AdminGenrePayload,
  AdminMoviePayload,
  AdminUserInfoResponse,
} from "../types/admin.types";
import type { Genre } from "../types/genre.types";
import type { MovieDTO } from "../types/movie.types";

export function addMovie(movie: AdminMoviePayload): Promise<MovieDTO> {
  return http<MovieDTO>("/admin/movie/add", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie),
  });
}

export function editMovie(
  movieId: number,
  movie: AdminMoviePayload,
): Promise<MovieDTO> {
  return http<MovieDTO>(`/admin/movie/${movieId}/edit`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie),
  });
}

export function deleteMovie(movieId: number): Promise<MovieDTO> {
  return http<MovieDTO>(`/admin/movie/${movieId}/delete`, {
    method: "DELETE",
  });
}

export function changeUserLockStatus(
  userId: number,
): Promise<AdminUserInfoResponse> {
  return http<AdminUserInfoResponse>(`/admin/user/${userId}/lock`, {
    method: "PUT",
  });
}

export function disableUser(userId: number): Promise<AdminUserInfoResponse> {
  return http<AdminUserInfoResponse>(`/admin/user/${userId}/disable`, {
    method: "PUT",
  });
}

export function addGenre(genre: AdminGenrePayload): Promise<Genre> {
  return http<Genre>("/admin/genre/add", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(genre),
  });
}

export function deleteGenre(genreId: number): Promise<Genre> {
  return http<Genre>(`/admin/genre/delete/${genreId}`, {
    method: "DELETE",
  });
}
