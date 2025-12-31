import type { MovieDTO } from "../../types/movie.types";
import styles from "./MovieCard.module.css";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

type Props = { movie: MovieDTO };

export function MovieCard({ movie }: Props) {
  const posterUrl = movie.posterPath ? `${TMDB_IMG_BASE}${movie.posterPath}` : null;

  return (
    <div className={styles.card}>
      <div className={styles.posterWrap}>
        {posterUrl ? (
          <img className={styles.poster} src={posterUrl} alt={movie.title} />
        ) : (
          <div className={styles.noPoster}>No poster</div>
        )}
      </div>

      <div className={styles.body}>
        <div className={styles.title}>{movie.title}</div>

        <div className={styles.meta}>
          <span>⭐ {movie.voteAverage ?? "—"}</span>
          <span>📅 {movie.releaseDate ?? "—"}</span>
        </div>
      </div>
    </div>
  );
}