import { useEffect, useState } from "react";
import { fetchMovies } from "../../api/movie.api";
import type { MovieAllResponse } from "../../types/movie.types";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import styles from "./MoviesPage.module.css";
import { Link } from "react-router-dom";

export function MoviesPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<MovieAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sort, setSort] = useState<"title" | "popularity" | "releaseDate" | "voteAverage">("title");
  const [order, setOrder] = useState<"asc" | "desc">("asc");

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetchMovies({ page: page - 1, size, order, sort })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [page, size, order, sort]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>

        <nav className={styles.nav}>
        <button
            className={`${styles.navItem} ${sort === "releaseDate" && order === "desc" ? styles.active : ""}`}
            onClick={() => { setSort("releaseDate"); setOrder("desc"); setPage(1); }}
            >
            Newest
        </button>

        <button
            className={`${styles.navItem} ${sort === "popularity" && order === "desc" ? styles.active : ""}`}
            onClick={() => { setSort("popularity"); setOrder("desc"); setPage(1); }}
            >
           Popular
        </button>

        <button
            className={`${styles.navItem} ${sort === "voteAverage" && order === "desc" ? styles.active : ""}`}
            onClick={() => { setSort("voteAverage"); setOrder("desc"); setPage(1); }}
            >
           Most Rated
        </button>
        </nav>

        <div className={styles.pager}>
          <button disabled={loading || page <= 1} onClick={() => setPage((p) => p - 1)}>
            Prev
          </button>

          <span>
            Page {data ? data.pageNumber + 1 : page} / {data?.totalPages ?? "?"}
          </span>

          <button
            disabled={loading || !data || data.lastPage}
            onClick={() => setPage((p) => p + 1)}
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