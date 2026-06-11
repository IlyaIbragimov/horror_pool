import { useCallback, useState } from "react";
import { useParams } from "react-router-dom";
import { getWatchlistItemsByWatchlistId } from "../../api/watchlist.api";
import { Pager } from "../../components/Pager/Pager";
import { WatchlistItemCard } from "../../components/WatchlistItemCard/WatchlistItemCard";
import { useAsyncResource } from "../../hooks/useAsyncResource";
import type { WatchlistItemsByWatchlistIdResponse } from "../../types/watchlist.types";
import styles from "./WatchlistPage.module.css";

export function WatchlistPage() {
  const { watchlistId } = useParams<{ watchlistId: string }>();
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const loadWatchlist = useCallback((): Promise<WatchlistItemsByWatchlistIdResponse> => {
    const id = Number(watchlistId);
    if (!Number.isFinite(id)) {
      return Promise.reject(new Error("Invalid watchlist id."));
    }

    return getWatchlistItemsByWatchlistId(id, {
      watched: undefined,
      page: page - 1,
      size,
      order: "asc",
    });
  }, [watchlistId, page, size]);

  const { data, loading, error, reload } = useAsyncResource(loadWatchlist);

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
        {data?.items.map((i) => (
          <div className={styles.card} key={i.movieDTO.movieId}>
            <WatchlistItemCard
              watchlistItem={i}
              onChanged={() => {
                reload();
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
