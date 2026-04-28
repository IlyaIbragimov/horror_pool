import { useCallback, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getWatchlistItemsByWatchlistId } from "../../api/watchlist.api";
import { Pager } from "../../components/Pager/Pager";
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
                refresh();
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
