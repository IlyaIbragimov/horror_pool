import type { WatchlistItemDTO } from "../../types/watchlist.types";
import styles from "./WatchlistItemCard.module.css";
import { Link } from "react-router-dom";
import {
  toggleWatchlistItem,
  removeMovieFromWatchlist,
} from "../../api/watchlist.api";
import { useState } from "react";
import { useAuth } from "../../auth/AuthContext";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

type Props = { watchlistItem: WatchlistItemDTO; onChanged?: () => void };

export function WatchlistItemCard({ watchlistItem, onChanged }: Props) {
  const posterUrl = watchlistItem.movieDTO.posterPath
    ? `${TMDB_IMG_BASE}${watchlistItem.movieDTO.posterPath}`
    : null;
  const { user, loading } = useAuth();
  const [itemWatchedLoading, setItemWatchedLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleItemToggle = async () => {
    if (itemWatchedLoading) return;
    setItemWatchedLoading(true);
    try {
      await toggleWatchlistItem(
        watchlistItem.watchlistId,
        watchlistItem.watchItemId,
      );
      onChanged?.();
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setItemWatchedLoading(false);
    }
  };

  const removeItem = async () => {
    if (itemWatchedLoading) return;
    setItemWatchedLoading(true);
    try {
      await removeMovieFromWatchlist(
        watchlistItem.watchlistId,
        watchlistItem.watchItemId,
      );
      onChanged?.();
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setItemWatchedLoading(false);
    }
  };

  return (
    <div className={styles.watchlistItem_card}>
      <Link
        key={watchlistItem.movieDTO.movieId}
        to={`/movies/${watchlistItem.movieDTO.movieId}`}
        className={styles.watchlistItem_card_posterWrap}
      >
        {posterUrl ? (
          <img
            className={styles.watchlistItem_card_poster}
            src={posterUrl}
            alt={watchlistItem.movieDTO.title}
          />
        ) : (
          <div className={styles.noPoster}>No image</div>
        )}
      </Link>

      <div className={styles.watchlistItem_card_content}>
        <Link to={`/movies/${watchlistItem.movieDTO.movieId}`}>
          <h3 className={styles.watchlistItem_card_title}>
            {watchlistItem.movieDTO.title}
          </h3>
        </Link>

        <div className={styles.watchlistItem_card_description}>
          <p>{watchlistItem.movieDTO.overview}</p>
        </div>

        {error && <div className={styles.error}>{error}</div>}
        {loading && <div>Loading...</div>}

        {!loading && user && (
          <div className={styles.watchlistItem_actions}>
            <div className={styles.watchlistItem_card_isWatched}>
              <button
                type="button"
                className={styles.watchlistItem_isWatched_btn}
                onClick={handleItemToggle}
                disabled={itemWatchedLoading}
              >
                {itemWatchedLoading
                  ? "Loading..."
                  : watchlistItem.watched
                    ? "Watched"
                    : "Unseen"}
              </button>
            </div>

            <div className={styles.watchlistItem_remove}>
              <button
                className={styles.watchlistItem_remove_btn}
                type="button"
                disabled={itemWatchedLoading}
                onClick={removeItem}
              >
                Remove
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
