import { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getWatchlistItemsByWatchlistId } from "../../api/watchlist.api";
import { WatchlistItemCard } from "../../components/WatchlistItemCard/WatchlistItemCard";
import type { WatchlistItemsByWatchlistIdResponse } from "../../types/watchlist.types";
import styles from "./WatchlistPage.module.css";

export function WatchlistPage() {
  const { watchlistId } = useParams<{ watchlistId: string }>();
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<WatchlistItemsByWatchlistIdResponse | null>(
    null,
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(() => {
    const id = Number(watchlistId);
    if (!Number.isFinite(id)) {
      setError("Invalid watchlist id.");
      setData(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    getWatchlistItemsByWatchlistId(id, {
      watched: undefined,
      page: page - 1,
      size,
      order: "asc",
    })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [watchlistId, page, size]);

  useEffect(() => {
    refresh();
  }, [refresh]);

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
        {data?.items.map((i) => (
          <div className={styles.card} key={i.movieDTO.movieId}>
            <WatchlistItemCard
              watchlistItem={i}
              onChanged={() => {
                refresh();
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
