import { notifyUserWatchlistsInvalidated } from "./cacheInvalidation";

export function invalidateUserWatchlists() {
  notifyUserWatchlistsInvalidated();
}
