import { http } from "./http";
import type {
  AdminGenrePayload,
  AdminMoviePayload,
  AdminUserInfoResponse,
  BulkImportResultResponse,
  TmdbDiscoverRequest,
} from "../types/admin.types";
import type { Genre } from "../types/genre.types";
import type { AdminMovieDTO } from "../types/movie.types";

export function addMovie(movie: AdminMoviePayload): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>("/admin/movie/add", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie),
  });
}

export function editMovie(
  movieId: number,
  movie: AdminMoviePayload,
): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>(`/admin/movie/${movieId}/edit`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie),
  });
}

export function deleteMovie(movieId: number): Promise<AdminMovieDTO> {
  return http<AdminMovieDTO>(`/admin/movie/${movieId}/delete`, {
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

export function editGenre(genreId: number, genreToEdit: Genre): Promise<Genre> {
  return http<Genre>(`/admin/genre/update/${genreId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(genreToEdit),
  });
}

export function bulkImportFromTmd(
  request?: TmdbDiscoverRequest,
): Promise<BulkImportResultResponse> {
  return http<BulkImportResultResponse>("/admin/tmdb/bulkImport", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: request ? JSON.stringify(request) : undefined,
  });
}
