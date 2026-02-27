import { useState } from "react";
import type { WatchlistDTO } from "../../types/watchlist.types";
import { useAuth } from "../../auth/AuthContext";
import { followWatchlist, unfollowWatchlist } from "../../api/watchlist.api";
import { Link } from "react-router-dom";
import styles from "./WatchlistCard.module.css";

type Props = { watchlist: WatchlistDTO; onChanged?: () => void };

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

export function WatchlistCard({ watchlist, onChanged }: Props) {
  const { user, loading } = useAuth();
  const [followLoading, setFollowLoading] = useState(false);
  const watchlistItemsCount = watchlist.watchlistItemDTOS.length ?? 0;
  const posters = watchlist.watchlistItemDTOS
    .map((i) => i.movieDTO.posterPath)
    .filter((p): p is string => Boolean(p))
    .slice(0, 7);

  const handleFollowToggle = async () => {
    if (followLoading) return;
    setFollowLoading(true);

    try {
      if (watchlist.followedByMe) {
        await unfollowWatchlist(watchlist.watchlistId);
      } else {
        await followWatchlist(watchlist.watchlistId);
      }

      onChanged?.();
    } catch (e) {
      console.error(e);
    } finally {
      setFollowLoading(false);
    }
  };

  return (
    <div className={styles.watchlist_card}>
      <Link
        className={styles.watchlist_posters}
        to={`/watchlist/${watchlist.watchlistId}`}
      >
        {posters.length ? (
          posters.map((p, idx) => (
            <img
              key={p + idx}
              className={styles.poster}
              src={`${TMDB_IMG_BASE}${p}`}
              alt=""
              style={{ zIndex: posters.length - idx }}
            />
          ))
        ) : (
          <div className={styles.noPosters}>No posters</div>
        )}
      </Link>

      <div className={styles.watchlist_content}>
        {loading ? null : user ? (
          <>
            <Link to={`/watchlist/${watchlist.watchlistId}`}>
              <h3 className={styles.title}>{watchlist.title}</h3>
            </Link>

            <div className={styles.watchlist_total_items}>
              Containig {watchlistItemsCount} movies
            </div>

            <div className={styles.watchlist_rating}>
              Raiting: {watchlist.rating} Total {watchlist.rateCount} users have
              voted
            </div>

            <div className={styles.watchlist_rate}>
              <p>Rate watchlist:</p>
              <ul className={styles.rate_ul}>
                {Array.from({ length: 10 }, (_, i) => (
                  <li key={i} className={styles.rate_item}>
                    <button type="button" className={styles.rate_btn}>
                      <svg
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                        className={styles.rate_img}
                      >
                        <g>
                          <path fill="none" d="M0 0h24v24H0z" />
                          <path d="M12 2c5.523 0 10 4.477 10 10v3.764a2 2 0 0 1-1.106 1.789L18 19v1a3 3 0 0 1-2.824 2.995L14.95 23a2.5 2.5 0 0 0 .044-.33L15 22.5V22a2 2 0 0 0-1.85-1.995L13 20h-2a2 2 0 0 0-1.995 1.85L9 22v.5c0 .171.017.339.05.5H9a3 3 0 0 1-3-3v-1l-2.894-1.447A2 2 0 0 1 2 15.763V12C2 6.477 6.477 2 12 2zm-4 9a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4z" />
                        </g>
                      </svg>
                    </button>
                  </li>
                ))}
              </ul>
            </div>

            <div className={styles.watchlist_actions}>
              <button
                type="button"
                className={styles.watchlist_action_btn}
                onClick={handleFollowToggle}
                disabled={followLoading}
              >
                {followLoading
                  ? "Loading..."
                  : watchlist.followedByMe
                    ? "Unfollow"
                    : "Follow"}
              </button>
            </div>
          </>
        ) : (
          <>
            <Link to={`/watchlist/${watchlist.watchlistId}`}>
              <h3 className={styles.title}>{watchlist.title}</h3>
            </Link>

            <div className={styles.watchlist_total_items}>
              Containig {watchlistItemsCount} movies
            </div>

            <div className={styles.watchlist_rating}>
              {watchlist.rating} total {watchlist.rateCount} have voted
            </div>
          </>
        )}
      </div>
    </div>
  );
}
