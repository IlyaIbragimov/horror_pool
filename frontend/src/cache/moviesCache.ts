import type { MovieAllResponse } from "../types/movie.types";
import { notifyMoviesInvalidated } from "./cacheInvalidation";

type MoviesCacheKeyParams = {
  page: number;
  size: number;
  sort: "title" | "popularity" | "releaseDate" | "voteAverage";
  order: "asc" | "desc";
  keyword?: string;
};

const moviesCache = new Map<string, MovieAllResponse>();

function buildCacheKey(params: MoviesCacheKeyParams) {
  return JSON.stringify(params);
}

export function getCachedMovies(params: MoviesCacheKeyParams) {
  return moviesCache.get(buildCacheKey(params));
}

export function setCachedMovies(
  params: MoviesCacheKeyParams,
  data: MovieAllResponse,
) {
  moviesCache.set(buildCacheKey(params), data);
}

export function invalidateMoviesCache() {
  moviesCache.clear();
  notifyMoviesInvalidated();
}
