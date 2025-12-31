// import { http } from "./http";
// import type { MovieAllResponse, MovieDTOLight } from "../types/movie.types";

// const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;

// export async function fetchMovies(params?: {
//   page?: number;
//   size?: number;
//   sort?: string;
//   order?: string;
// }): Promise<MovieAllResponse> {
//   const search = new URLSearchParams();

//   if (params?.page !== undefined) search.set("page", String(params.page));
//   if (params?.size !== undefined) search.set("size", String(params.size));
//   if (params?.sort) search.set("sort", params.sort);
//   if (params?.order) search.set("order", params.order);

//   const url = `${API_BASE_URL}/public/movie/all?${search.toString()}`;

//   const res = await fetch(url, {
//     method: "GET",
//     headers: { Accept: "application/json" },
//   });

//   if (!res.ok) {
//     const text = await res.text().catch(() => "");
//     throw new Error(`Failed to fetch movies: ${res.status} ${res.statusText} ${text}`);
//   }

//   return res.json();
// }

import { http } from "./http";
import type { MovieAllResponse } from "../types/movie.types";

export type MoviesQuery = {
  page?: number;
  size?: number;
  sort?: string;
  order?: string;
};

export function fetchMovies(params: MoviesQuery = {}): Promise<MovieAllResponse> {
  const search = new URLSearchParams();

  if (params.page !== undefined) search.set("page", String(params.page));
  if (params.size !== undefined) search.set("size", String(params.size));
  if (params.sort) search.set("sort", params.sort);
  if (params.order) search.set("order", params.order);

  const qs = search.toString();
  const url = `/public/movie/all${qs ? `?${qs}` : ""}`;

  return http<MovieAllResponse>(url);
}