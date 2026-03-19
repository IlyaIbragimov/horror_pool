import { useEffect, useState } from "react";
import { getAllPublicWatchlists } from "../../api/watchlist.api";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import styles from "./PublicWatchlistPage.module.css";

const publicWatchlistsCache = new Map<string, WatchlistAllResponse>();

function buildCacheKey(params: {
  page: number;
  size: number;
  order: "asc" | "desc";
}) {
  return JSON.stringify(params);
}

export function PublicWatchlistPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<WatchlistAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = (force = false) => {
    const params = { page: page - 1, size, order: "asc" as const };
    const cacheKey = buildCacheKey(params);

    if (!force) {
      const cachedData = publicWatchlistsCache.get(cacheKey);
      if (cachedData) {
        setData(cachedData);
        setError(null);
        setLoading(false);
        return;
      }
    }

    setLoading(true);
    setError(null);
    getAllPublicWatchlists(params)
      .then((result) => {
        publicWatchlistsCache.set(cacheKey, result);
        setData(result);
      })
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    refresh();
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
          <div key={w.watchlistId} className={styles.cardLink}>
            <WatchlistCard
              watchlist={w}
              onChanged={() => {
                refresh(true);
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
