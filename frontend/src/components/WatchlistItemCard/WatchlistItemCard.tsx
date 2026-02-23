import type { WatchlistItemDTO } from "../../types/watchlist.types";
import styles from "./WatchlistItemCard.module.css";
import { Link } from "react-router-dom";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

type Props = { watchlistItem: WatchlistItemDTO };

export function WatchlistItemCard({ watchlistItem }: Props) {
  const posterUrl = watchlistItem.movieDTO.posterPath
    ? `${TMDB_IMG_BASE}${watchlistItem.movieDTO.posterPath}`
    : null;

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
        <div className={styles.watchlistItem_card_isWatched}>
          <label className={styles.watchlistItem_checkboxLabel}>
            <input
              type="checkbox"
              checked={Boolean(watchlistItem.watched)}
              readOnly
            />
            <span>Watched</span>
          </label>
        </div>
      </div>
    </div>
  );
}
