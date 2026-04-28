import { useEffect, useState } from "react";
import { getAllPublicWatchlists } from "../../api/watchlist.api";
import {
  getCachedPublicWatchlists,
  setCachedPublicWatchlists,
} from "../../cache/publicWatchlistsCache";
import { subscribePublicWatchlistsInvalidation } from "../../cache/cacheInvalidation";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import { Pager } from "../../components/Pager/Pager";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import styles from "./PublicWatchlistPage.module.css";

export function PublicWatchlistPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<WatchlistAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = (force = false) => {
    const params = { page: page - 1, size, order: "asc" as const };

    if (!force) {
      const cachedData = getCachedPublicWatchlists(params);
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
        setCachedPublicWatchlists(params, result);
        setData(result);
      })
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    refresh();
  }, [page, size]);

  useEffect(() => {
    return subscribePublicWatchlistsInvalidation(() => {
      refresh(true);
    });
  }, [page, size]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <Pager
          className={styles.pager}
          loading={loading}
          page={page}
          pageNumber={data ? data.pageNumber + 1 : undefined}
          totalPages={data?.totalPages}
          lastPage={data?.lastPage}
          onPageChange={setPage}
        />
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
