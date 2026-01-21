import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type { MovieDTO } from "../../types/movie.types";
import { fetchMovieById, addCommentToMovie } from "../../api/movie.api";
import styles from "./MoviePage.module.css";
import { CommentCard } from "../../components/CommentCard/CommentCard";
import { useAuth } from "../../auth/AuthContext";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w500";

export function MoviePage() {
  const { movieId } = useParams();

  const [movie, setMovie] = useState<MovieDTO | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [commentContent, setCommentContent] = useState("");
  const { user, loading: authLoading } = useAuth();


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

  const onSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      if (!movieId) return;
      if (!user) {
        setError("Please sign in to add a comment.");
        return;
      }
      const text = commentContent.trim();
      if (!text) return;
      setLoading(true);
      setError(null);
      try {
        const updatedMovie = await addCommentToMovie(Number(movieId), text);
        setMovie(updatedMovie); 
        setCommentContent("");  
      } catch (e) {
        setError(e instanceof Error ? e.message : "Adding comment failed");
      } finally {
        setLoading(false);
      }
  };

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
        {!authLoading && !user ? (
          <div className={styles.comment_login_hint}>
            Please sign in to add a comment.
          </div>
        ) : (
          <form className={styles.comment_form} onSubmit={onSubmit}>
            <textarea
              className={styles.comment_input}
              value={commentContent}
              onChange={(e) => setCommentContent(e.target.value)}
              placeholder="Write a comment..."
            />
            <button type="submit" className={styles.comment_button} disabled={loading}>
              Add
            </button>
          </form>
        )}
        <div className={styles.comments}>
        {movie?.comments.map((c) => (
          <CommentCard key={c.commentId} comment={c} />
        ))}
        </div>
      </div>
    </div>
  );
}