import { http } from "./http";
import type {
  WatchlistDTO,
  WatchlistItemDTO,
  WatchlistAllResponse,
  WatchlistQuery,
  WatchlistItemsByWatchlistIdQuery,
  WatchlistItemsByWatchlistIdResponse
} from "../types/watchlist.types";

export function getAllPublicWatchlists(
  params: WatchlistQuery = {}
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
  params: WatchlistItemsByWatchlistIdQuery = {}
): Promise<WatchlistItemsByWatchlistIdResponse> {
  const search = new URLSearchParams();

  if (params.watched !== undefined) search.set("watched", String(params.watched));
  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/watchlist/${watchlistId}${qs ? `?${qs}` : ""}`;

  return http<WatchlistItemsByWatchlistIdResponse>(url);
}

export function followWatchlist(watchlistId: number): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/follow`, {
    method: "PUT"
  });
}

export function unfollowWatchlist(watchlistId: number): Promise<WatchlistDTO> {
  return http<WatchlistDTO>(`/user/watchlist/${watchlistId}/unfollow`, {
    method: "PUT"
  });
}
