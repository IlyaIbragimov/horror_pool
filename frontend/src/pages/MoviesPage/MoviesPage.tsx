import { useEffect, useState } from "react";
import { fetchMovies, searchMovie } from "../../api/movie.api";
import type { MovieAllResponse } from "../../types/movie.types";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import styles from "./MoviesPage.module.css";
import { Link, useSearchParams } from "react-router-dom";

export function MoviesPage() {
  
  const [data, setData] = useState<MovieAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sort] = useState<"title" | "popularity" | "releaseDate" | "voteAverage">("title");
  const [order] = useState<"asc" | "desc">("asc");

  const [searchParams, setSearchParams] = useSearchParams();

  const page = Number(searchParams.get("page") ?? "1");
  const keyword = searchParams.get("keyword") ?? undefined;
  const pageParam = Number(searchParams.get("page") ?? "1");
  const sizeParam = Number(searchParams.get("size") ?? "18");
  const sortParam = (searchParams.get("sort") ?? "title") as "title" | "popularity" | "releaseDate" | "voteAverage";
  const orderParam = (searchParams.get("order") ?? "asc") as "asc" | "desc";

  const setQuery = (patch: Record<string, string>) => {
  const next = new URLSearchParams(searchParams);
  Object.entries(patch).forEach(([k, v]) => next.set(k, v));
  setSearchParams(next);
  };

  const goToPage = (p: number) => setQuery({ page: String(p) });

  useEffect(() => {
    setLoading(true);
    setError(null);

    const paramsBase = { page: pageParam - 1, size: sizeParam, sort: sortParam, order: orderParam };

    const promise = keyword
      ? searchMovie({ ...paramsBase, keyword })
      : fetchMovies(paramsBase);

    promise
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [keyword, pageParam, sizeParam, sortParam, orderParam]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>

        <nav className={styles.nav}>
        <button
            className={`${styles.navItem} ${sort === "releaseDate" && order === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "releaseDate", order: "desc", page: "1" })}
            >
            Newest
        </button>

        <button
            className={`${styles.navItem} ${sort === "popularity" && order === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "popularity", order: "desc", page: "1" })}
            >
           Popular
        </button>

        <button
            className={`${styles.navItem} ${sort === "voteAverage" && order === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "voteAverage", order: "desc", page: "1" })}
            >
           Most Rated
        </button>
        </nav>

        <div className={styles.pager}>
          <button disabled={loading || page <= 1} onClick={() => goToPage(page - 1)}>
            Prev
          </button>

          <span>
            Page {data ? data.pageNumber + 1 : page} / {data?.totalPages ?? "?"}
          </span>

          <button
            disabled={loading || !data || data.lastPage}
            onClick={() => goToPage(page + 1)}
          >
            Next
          </button>
        </div>
      </div>

      {error && <div className={styles.error}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div className={styles.grid}>
        {data?.movies.map((m) => (
         <Link key={m.movieId} to={`/movies/${m.movieId}`} className={styles.cardLink}>
            <MovieCard key={m.movieId} movie={m} />
         </Link>
        ))}
      </div>
    </div>
  );
}