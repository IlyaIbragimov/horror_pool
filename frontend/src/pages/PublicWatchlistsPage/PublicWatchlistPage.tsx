import { useCallback, useEffect, useRef, useState } from "react";
import { getAllPublicWatchlists } from "../../api/watchlist.api";
import {
  getCachedPublicWatchlists,
  setCachedPublicWatchlists,
} from "../../cache/publicWatchlistsCache";
import { subscribePublicWatchlistsInvalidation } from "../../cache/cacheInvalidation";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import { Pager } from "../../components/Pager/Pager";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import { useAsyncResource } from "../../hooks/useAsyncResource";
import styles from "./PublicWatchlistPage.module.css";

export function PublicWatchlistPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);
  const forceReload = useRef(false);

  const loadWatchlists = useCallback((): Promise<WatchlistAllResponse> => {
    const params = { page: page - 1, size, order: "asc" as const };
    const cachedData = getCachedPublicWatchlists(params);
    const shouldUseCache = !forceReload.current;
    forceReload.current = false;

    if (shouldUseCache && cachedData) {
      return Promise.resolve(cachedData);
    }

    return getAllPublicWatchlists(params)
      .then((result) => {
        setCachedPublicWatchlists(params, result);
        return result;
      })
  }, [page, size]);

  const { data, loading, error, reload } = useAsyncResource(loadWatchlists);
  const refresh = useCallback(() => {
    forceReload.current = true;
    return reload();
  }, [reload]);

  useEffect(() => {
    return subscribePublicWatchlistsInvalidation(() => {
      refresh();
    });
  }, [refresh]);

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
                refresh();
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
