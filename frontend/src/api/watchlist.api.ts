import { http } from "./http";
import type {
  WatchlistDTO,
  WatchlistItemDTO,
  WatchlistAllResponse,
  WatchlistQuery,
  WatchlistItemsByWatchlistIdQuery,
  WatchlistItemsByWatchlistIdResponse,
} from "../types/watchlist.types";

export function getAllPublicWatchlists(
  params: WatchlistQuery = {},
): Promise<WatchlistAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/watchlist/allPublic${qs ? `?${qs}` : ""}`;

  return http<WatchlistAllResponse>(url);
}

export function getWatchlistItemsByWatchlistId(
  watchlistId: number,
  params: WatchlistItemsByWatchlistIdQuery = {},
): Promise<WatchlistItemsByWatchlistIdResponse> {
  const search = new URLSearchParams();

  if (params.watched !== undefined)
    search.set("watched", String(params.watched));
  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/watchlist/${watchlistId}${qs ? `?${qs}` : ""}`;

  return http<WatchlistItemsByWatchlistIdResponse>(url);
}

export function followWatchlist(watchlistId: number): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/follow`, {
    method: "PUT",
  });
}

export function unfollowWatchlist(watchlistId: number): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/unfollow`, {
    method: "PUT",
  });
}

export function getAllUserWatchlists(
  params: WatchlistQuery = {},
): Promise<WatchlistAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);
  const qs = search.toString();
  const url = `/user/watchlist/allByUser${qs ? `?${qs}` : ""}`;

  return http<WatchlistAllResponse>(url);
}

export function getRatedWatchlistsByUser(
  params: WatchlistQuery = {},
): Promise<WatchlistAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);
  const qs = search.toString();
  const url = `/user/watchlist/rated${qs ? `?${qs}` : ""}`;

  return http<WatchlistAllResponse>(url);
}

export function getFollowedWatchlists(
  params: WatchlistQuery = {},
): Promise<WatchlistAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);
  const qs = search.toString();
  const url = `/user/watchlist/followed${qs ? `?${qs}` : ""}`;

  return http<WatchlistAllResponse>(url);
}

export function rateWatchlist(
  watchlistId: number,
  rating: number,
): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/rate`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ rating }),
  });
}

export function toggleWatchlistItem(
  watchlistId: number,
  watchlistItemId: number,
): Promise<WatchlistItemDTO> {
  return http<WatchlistItemDTO>(
    `/user/watchlist/${watchlistId}/toggle/${watchlistItemId}`, {
      method: "PUT"
    });
}

export function createWatchlist(
  title: string,
  isPublic: boolean,
): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, public: isPublic }),
  });
}

export function addMovieToWatchlist(
  watchlistId: number,
  movieId: number,
): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/add/${movieId}`, {
    method: "POST"
  });
}

export function removeMovieFromWatchlist(
  watchlistId: number,
  watchlistItemId: number,
): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/remove/${watchlistItemId}`, {
    method: "DELETE"
  });
}