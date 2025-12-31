import { useEffect, useState } from "react";
import { fetchMovies } from "../../api/movie.api";
import type { MovieAllResponse } from "../../types/movie.types";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import styles from "./MoviesPage.module.css";

export function MoviesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(12);

  const [data, setData] = useState<MovieAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetchMovies({ page, size, order: "asc", sort: "title" })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [page, size]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <h2>Movies</h2>

        <div className={styles.pager}>
          <button disabled={loading || page <= 0} onClick={() => setPage((p) => p - 1)}>
            Prev
          </button>

          <span>
            Page {data?.pageNumber ?? page} / {data?.totalPages ?? "?"}
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
          <MovieCard key={m.movieId} movie={m} />
        ))}
      </div>
    </div>
  );
}