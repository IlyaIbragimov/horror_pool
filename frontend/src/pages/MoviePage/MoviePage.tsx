import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type { MovieDTO } from "../../types/movie.types";
import { fetchMovieById } from "../../api/movie.api";
import styles from "./MoviePage.module.css";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w500";

export function MoviePage() {
  const { movieId } = useParams();

  const [movie, setMovie] = useState<MovieDTO | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [comment, setComment] = useState("");

  useEffect(() => {
    if (!movieId) return;

    const id = Number(movieId);
    if (Number.isNaN(id)) {
      setError("Invalid movie id");
      return;
    }

    setLoading(true);
    setError(null);

    fetchMovieById(id)
      .then(setMovie)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [movieId]);

  if (loading) return <div className={styles.page}>Loading...</div>;
  if (error) return <div className={styles.page} style={{ color: "#ff6b6b" }}>{error}</div>;
  if (!movie) return <div className={styles.page}>Movie not found</div>;

  const posterUrl = movie.posterPath ? `${TMDB_IMG_BASE}${movie.posterPath}` : null;

  return (
    <div className={styles.page}>
      <div className={styles.movie_section}>
        <Link className={styles.back} to="/movies">← Back to movies</Link>

        <div className={styles.header}>
          {posterUrl ? (
            <img className={styles.poster} src={posterUrl} alt={movie.title} />
          ) : (
            <div className={styles.poster} />
          )}

          <div>
            <h2 className={styles.title}>{movie.title}</h2>

            <div className={styles.metaRow}>
              <span className={styles.badge}>⭐ {movie.voteAverage ?? "-"}</span>
              <span className={styles.badge}>📅 {movie.releaseDate ?? "-"}</span>
              <span className={styles.badge}>🗣️ {movie.originalLanguage ?? "-"}</span>
              <span className={styles.badge}>👥 votes {movie.voteCount ?? "-"}</span>
            </div>

            {movie.overview && <div className={styles.overview}>{movie.overview}</div>}

            <div className={styles.kv}>
              <div className={styles.k}>Release year</div>
              <div>{movie.releaseYear ?? "-"}</div>

              <div className={styles.k}>Popularity</div>
              <div>{movie.popularity ?? "-"}</div>
            </div>
          </div>
        </div>
      </div>

      <div className={styles.comment_section}>
        <form className={styles.comment_form}>
          <textarea className={styles.comment_input} value={comment} onChange={(e) => setComment(e.target.value)} placeholder="Write review..." aria-label="Write review"/>
          <button className={styles.comment_button}>Add</button>
        </form>
      </div>
      
    </div>
  );
}