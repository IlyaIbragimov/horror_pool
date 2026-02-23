import { useEffect, useState } from "react";
import { getAllPublicWatchlists } from "../../api/watchlist.api";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import styles from "./PublicWatchlistPage.module.css";

export function PublicWatchlistPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<WatchlistAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    getAllPublicWatchlists({ page: page - 1, size, order: "asc"})
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [page, size]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.pager}>
          <button
            disabled={loading || page <= 1}
            onClick={() => setPage((p) => p - 1)}
          >
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

      <div className={styles.list}>
        {data?.watchlistDTOS.map((w) => (
          <div
            key={w.watchlistId}
            className={styles.cardLink}
          >
            <WatchlistCard key={w.watchlistId} watchlist={w} />
          </div>
        ))}
      </div>
    </div>
  );
}
