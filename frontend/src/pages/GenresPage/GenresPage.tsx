import { useEffect, useState } from "react";
import { fetchGenres } from "../../api/genre.api";
import type { GenreAllResponse } from "../../types/genre.types";
import { GenreCard } from "../../components/GenreCard/GenreCard";
import styles from "./GenresPage.module.css";
import { Link } from "react-router-dom";

export function GenresPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<GenreAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
 

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetchGenres({ page: page - 1, size, order: "asc", sort: "name" })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [page, size]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>

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
        {data?.genres.map((g) => (
         <Link key={g.genreId} to={`/genres/${g.genreId}`} className={styles.cardLink}>
            <GenreCard key={g.genreId} genre={g} />
         </Link>
        ))}
      </div>
    </div>
  );
}