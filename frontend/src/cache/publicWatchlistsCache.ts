import type { WatchlistAllResponse } from "../types/watchlist.types";
import { notifyPublicWatchlistsInvalidated } from "./cacheInvalidation";

type PublicWatchlistsCacheKeyParams = {
  page: number;
  size: number;
  order: "asc" | "desc";
};

const publicWatchlistsCache = new Map<string, WatchlistAllResponse>();

function buildCacheKey(params: PublicWatchlistsCacheKeyParams) {
  return JSON.stringify(params);
}

export function getCachedPublicWatchlists(
  params: PublicWatchlistsCacheKeyParams,
) {
  return publicWatchlistsCache.get(buildCacheKey(params));
}

export function setCachedPublicWatchlists(
  params: PublicWatchlistsCacheKeyParams,
  data: WatchlistAllResponse,
) {
  publicWatchlistsCache.set(buildCacheKey(params), data);
}

export function invalidatePublicWatchlistsCache() {
  publicWatchlistsCache.clear();
  notifyPublicWatchlistsInvalidated();
}
