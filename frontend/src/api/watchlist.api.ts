import { http } from "./http";
import type {
  WatchlistDTO,
  WatchlistItemDTO,
  WatchlistAllResponse,
  WatchlistQuery
} from "../types/watchlist.types";

export function getAllPublicWatchlists(
  params: WatchlistQuery = {},
): Promise<WatchlistAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/watchlist/allPublic${qs ? `?${qs}` : ""}`;

  return http<WatchlistAllResponse>(url);
}