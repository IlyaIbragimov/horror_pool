import { useEffect, useState } from "react";
import { useParams} from "react-router-dom";
import { getWatchlistItemsByWatchlistId } from "../../api/watchlist.api";
import { WatchlistItemCard } from "../../components/WatchlistItemCard/WatchlistItemCard";
import type { WatchlistItemsByWatchlistIdResponse, WatchlistDTO } from "../../types/watchlist.types";
import styles from "./WatchlistPage.module.css";

export function WatchlistPage() {
  const { watchlistId } = useParams<{ watchlistId: string }>();
  const [watchlist] = useState<WatchlistDTO | null>(null);
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<WatchlistItemsByWatchlistIdResponse | null>(
    null,
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    const id = Number(watchlistId);
    if (!Number.isFinite(id)) return;

    getWatchlistItemsByWatchlistId(id, {
      watched: undefined,
      page: page - 1,
      size,
      order: "asc",
    })
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

      <div>
        <h3>{watchlist?.title}</h3>
      </div>

      <div className={styles.list}>
        {data?.items.map((i) => (
          <div className={styles.card}>
            <WatchlistItemCard key={i.movieDTO.movieId} watchlistItem={i} />
          </div>
        ))}
      </div>
    </div>
  );
}
