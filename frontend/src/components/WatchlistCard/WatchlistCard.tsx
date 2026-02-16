import type { WatchlistDTO } from "../../types/watchlist.types";
import { useAuth } from "../../auth/AuthContext";
import styles from "./WatchlistCard.module.css";

type Props = { watchlist: WatchlistDTO };

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

export function WatchlistCard({watchlist }: Props) {
  const { user, loading } = useAuth();
  const watchlistItemsCount = watchlist.watchlistItemDTOS.length ?? 0;
  const posters = watchlist.watchlistItemDTOS
    .map((i) => i.movieDTO.posterPath)
    .filter((p): p is string => Boolean(p))
    .slice(0, 7);

  return (
    <div className={styles.watchlist_card}>

      <div className={styles.watchlist_posters}>
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
      </div>

      <div className={styles.watchlist_content}>
        {loading ? null : user ? (
                      <>
            <h3 className={styles.title}>{watchlist.title}</h3>
            <div className={styles.watchlist_total_items}>
              Containig {watchlistItemsCount} movies
            </div>
            <div className={styles.watchlist_rating}>
              Raiting: {watchlist.rating} Total {watchlist.rateCount} users have voted
            </div>
            <div className={styles.watchlist_actions}>
              <button className={styles.watchlist_add}>Add</button>
              <div className={styles.watchlist_rate}></div>
            </div>
          </>

        ) : (
                          <>
            <h3 className={styles.title}>{watchlist.title}</h3>
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
