import { useEffect, useState } from "react";
import {
  getAllUserWatchlists,
  getFollowedWatchlists,
  deleteWatchlist,
  renameWatchlist,
} from "../../api/watchlist.api";
import { subscribeUserWatchlistsInvalidation } from "../../cache/cacheInvalidation";
import { invalidatePublicWatchlistsCache } from "../../cache/publicWatchlistsCache";
import { invalidateUserWatchlists } from "../../cache/userWatchlistsInvalidation";
import { Pager } from "../../components/Pager/Pager";
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
    return getAllUserWatchlists({ page: myPage - 1, size, order: "asc" })
      .then(setMyData)
      .catch((e) => setMyError(e instanceof Error ? e.message : String(e)))
      .finally(() => setMyLoading(false));
  };

  const refreshFollowed = () => {
    setFollowedLoading(true);
    setFollowedError(null);
    return getFollowedWatchlists({ page: followedPage - 1, size, order: "asc" })
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

  useEffect(() => {
    return subscribeUserWatchlistsInvalidation(() => {
      refreshMy();
      refreshFollowed();
    });
  }, [myPage, followedPage, size]);

  const handleDeleteWatchlist = async (watchlistId: number) => {
    if (!watchlistId) return;
    if (!window.confirm("Delete this watchlist?")) return;
    setMyLoading(true);
    setMyError(null);
    try {
      await deleteWatchlist(watchlistId);
      invalidateUserWatchlists();
      invalidatePublicWatchlistsCache();
      await Promise.all([refreshMy(), refreshFollowed()]);
    } catch (e) {
      setMyError(e instanceof Error ? e.message : String(e));
    } finally {
      setMyLoading(false);
      setFollowedLoading(false);
    }
  };

  const handleRenameWatchlist = async (
    watchlistId: number,
    updatedTitle: string,
  ) => {
    if (!watchlistId) return;
    setMyLoading(true);
    setMyError(null);
    try {
      await renameWatchlist(watchlistId, updatedTitle);
      invalidateUserWatchlists();
      invalidatePublicWatchlistsCache();
      await Promise.all([refreshMy(), refreshFollowed()]);
    } catch (e) {
      setMyError(e instanceof Error ? e.message : String(e));
    } finally {
      setMyLoading(false);
      setFollowedLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>My Watchlists</h2>

        <Pager
          className={styles.pager}
          loading={myLoading}
          page={myPage}
          pageNumber={myData ? myData.pageNumber + 1 : undefined}
          totalPages={myData?.totalPages}
          lastPage={myData?.lastPage}
          onPageChange={setMyPage}
        />

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
                canRename={true}
                onRename={(updatedTitle) =>
                  handleRenameWatchlist(w.watchlistId, updatedTitle)
                }
              />
              <div className={styles.list_actions_owner}>
                <button
                  className={styles.owner_actions_delete}
                  onClick={() => handleDeleteWatchlist(w.watchlistId)}
                >
                  Delete Watchlist
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Followed Watchlists</h2>

        <Pager
          className={styles.pager}
          loading={followedLoading}
          page={followedPage}
          pageNumber={followedData ? followedData.pageNumber + 1 : undefined}
          totalPages={followedData?.totalPages}
          lastPage={followedData?.lastPage}
          onPageChange={setFollowedPage}
        />

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
