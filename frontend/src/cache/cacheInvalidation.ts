type CacheInvalidationListener = () => void;

const movieListeners = new Set<CacheInvalidationListener>();
const publicWatchlistListeners = new Set<CacheInvalidationListener>();
const userWatchlistListeners = new Set<CacheInvalidationListener>();

function subscribe(
  listeners: Set<CacheInvalidationListener>,
  listener: CacheInvalidationListener,
) {
  listeners.add(listener);
  return () => {
    listeners.delete(listener);
  };
}

function notify(listeners: Set<CacheInvalidationListener>) {
  listeners.forEach((listener) => listener());
}

export function subscribeMoviesInvalidation(
  listener: CacheInvalidationListener,
) {
  return subscribe(movieListeners, listener);
}

export function notifyMoviesInvalidated() {
  notify(movieListeners);
}

export function subscribePublicWatchlistsInvalidation(
  listener: CacheInvalidationListener,
) {
  return subscribe(publicWatchlistListeners, listener);
}

export function notifyPublicWatchlistsInvalidated() {
  notify(publicWatchlistListeners);
}

export function subscribeUserWatchlistsInvalidation(
  listener: CacheInvalidationListener,
) {
  return subscribe(userWatchlistListeners, listener);
}

export function notifyUserWatchlistsInvalidated() {
  notify(userWatchlistListeners);
}
