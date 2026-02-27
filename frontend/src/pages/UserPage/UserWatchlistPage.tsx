import { useEffect, useState } from "react";
import {
  getAllUserWatchlists,
  getFollowedWatchlists,
} from "../../api/watchlist.api";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import styles from "./UserWatchlistPage.module.css";

export function UserWatchlistPage() {
  const [size] = useState(4);

  const [myPage, setMyPage] = useState(1);
  const [myData, setMyData] = useState<WatchlistAllResponse | null>(null);
  const [myLoading, setMyLoading] = useState(false);
  const [myError, setMyError] = useState<string | null>(null);

  const [followedPage, setFollowedPage] = useState(1);
  const [followedData, setFollowedData] = useState<WatchlistAllResponse | null>(
    null,
  );
  const [followedLoading, setFollowedLoading] = useState(false);
  const [followedError, setFollowedError] = useState<string | null>(null);

  const refreshMy = () => {
    setMyLoading(true);
    setMyError(null);
    getAllUserWatchlists({ page: myPage - 1, size, order: "asc" })
      .then(setMyData)
      .catch((e) => setMyError(e instanceof Error ? e.message : String(e)))
      .finally(() => setMyLoading(false));
  };

  const refreshFollowed = () => {
    setFollowedLoading(true);
    setFollowedError(null);
    getFollowedWatchlists({ page: followedPage - 1, size, order: "asc" })
      .then(setFollowedData)
      .catch((e) =>
        setFollowedError(e instanceof Error ? e.message : String(e)),
      )
      .finally(() => setFollowedLoading(false));
  };

  useEffect(() => {
    refreshMy();
  }, [myPage, size]);

  useEffect(() => {
    refreshFollowed();
  }, [followedPage, size]);

  return (
    <div className={styles.page}>
      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>My Watchlists</h2>

        <div className={styles.pager}>
          <button
            disabled={myLoading || myPage <= 1}
            onClick={() => setMyPage((p) => p - 1)}
          >
            Prev
          </button>
          <span>
            Page {myData ? myData.pageNumber + 1 : myPage} /{" "}
            {myData?.totalPages ?? "?"}
          </span>
          <button
            disabled={myLoading || !myData || myData.lastPage}
            onClick={() => setMyPage((p) => p + 1)}
          >
            Next
          </button>
        </div>

        {myError && <div className={styles.error}>{myError}</div>}
        {myLoading && <div>Loading...</div>}

        <div className={styles.list}>
          {myData?.watchlistDTOS.map((w) => (
            <div className={styles.card} key={w.watchlistId}>
              <WatchlistCard
                watchlist={w}
                onChanged={() => {
                  refreshMy();
                  refreshFollowed();
                }}
              />
            </div>
          ))}
        </div>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Followed Watchlists</h2>

        <div className={styles.pager}>
          <button
            disabled={followedLoading || followedPage <= 1}
            onClick={() => setFollowedPage((p) => p - 1)}
          >
            Prev
          </button>
          <span>
            Page {followedData ? followedData.pageNumber + 1 : followedPage} /{" "}
            {followedData?.totalPages ?? "?"}
          </span>
          <button
            disabled={followedLoading || !followedData || followedData.lastPage}
            onClick={() => setFollowedPage((p) => p + 1)}
          >
            Next
          </button>
        </div>

        {followedError && <div className={styles.error}>{followedError}</div>}
        {followedLoading && <div>Loading...</div>}

        <div className={styles.list}>
          {followedData?.watchlistDTOS.map((w) => (
            <div key={w.watchlistId} className={styles.card}>
              <WatchlistCard
                watchlist={w}
                onChanged={() => {
                  refreshMy();
                  refreshFollowed();
                }}
              />
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
