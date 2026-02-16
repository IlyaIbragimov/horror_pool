import type { Genre } from "../../types/genre.types";
import styles from "./GenreCard.module.css";

const PLACEHOLDER_IMG_BASE = "https://firstbenefits.org/workers-compensation-wholesale-and-retail-trade/placeholder/";

type Props = { genre: Genre };

export function GenreCard({ genre }: Props) {
  const posterUrl = genre.posterPath ? `${PLACEHOLDER_IMG_BASE}${genre.posterPath}` : null;

  return (
    <div className={styles.card}>
      <div className={styles.posterWrap}>
        {posterUrl ? (
          <img className={styles.poster} src={posterUrl} alt={genre.name} />
        ) : (
          <div className={styles.noPoster}>No image</div>
        )}
      </div>

      <div className={styles.content}>
        <h3 className={styles.title}>{genre.name}</h3>
        <p className={styles.description}>
          {genre.description ?? "No description"}
        </p>
      </div>
    </div>
  );
}