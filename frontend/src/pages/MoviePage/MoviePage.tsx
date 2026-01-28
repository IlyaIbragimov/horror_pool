import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type { MovieDTO } from "../../types/movie.types";
import {
  fetchMovieById,
  addCommentToMovie,
  replyToComment,
} from "../../api/movie.api";
import styles from "./MoviePage.module.css";
import { CommentCard } from "../../components/CommentCard/CommentCard";
import { useAuth } from "../../auth/AuthContext";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w500";

export function MoviePage() {
  const { movieId } = useParams();

  const [movie, setMovie] = useState<MovieDTO | null>(null);
  const [pageLoading, setPageLoading] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [commentContent, setCommentContent] = useState("");
  const { user, loading: authLoading } = useAuth();
  const [replyToId, setReplyToId] = useState<number | null>(null);
  const [replyText, setReplyText] = useState("");

  const openReply = (commentId: number) => {
    setReplyToId(commentId);
    setReplyText("");
  };

  const closeReply = () => {
    setReplyToId(null);
    setReplyText("");
  };

  const submitReply = async (parentCommentId: number) => {
    if (!movieId) return;
    if (!user) {
      setError("Please sign in to reply.");
      return;
    }
    const text = replyText.trim();
    if (!text) return;

    setSubmitLoading(true);
    setError(null);
    try {
      const updatedMovie = await replyToComment(
        Number(movieId),
        parentCommentId,
        text,
      );
      setMovie(updatedMovie);
      closeReply();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Reply failed");
    } finally {
      setSubmitLoading(false);
    }
  };

  useEffect(() => {
    if (!movieId) return;

    const id = Number(movieId);
    if (Number.isNaN(id)) {
      setError("Invalid movie id");
      return;
    }

    setPageLoading(true);
    setError(null);

    fetchMovieById(id)
      .then(setMovie)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setPageLoading(false));
  }, [movieId]);

  if (pageLoading) return <div className={styles.page}>Loading...</div>;
  if (error)
    return (
      <div className={styles.page} style={{ color: "#ff6b6b" }}>
        {error}
      </div>
    );
  if (!movie) return <div className={styles.page}>Movie not found</div>;

  const posterUrl = movie.posterPath
    ? `${TMDB_IMG_BASE}${movie.posterPath}`
    : null;

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!movieId) return;
    if (!user) {
      setError("Please sign in to add a comment.");
      return;
    }
    const text = commentContent.trim();
    if (!text) return;
    setSubmitLoading(true);
    setError(null);
    try {
      const updatedMovie = await addCommentToMovie(Number(movieId), text);
      setMovie(updatedMovie);
      setCommentContent("");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Adding comment failed");
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.movie_section}>
        <Link className={styles.back} to="/movies">
          ← Back to movies
        </Link>

        <div className={styles.header}>
          {posterUrl ? (
            <img className={styles.poster} src={posterUrl} alt={movie.title} />
          ) : (
            <div className={styles.poster} />
          )}

          <div>
            <h2 className={styles.title}>{movie.title}</h2>

            <div className={styles.metaRow}>
              <span className={styles.badge}>
                ⭐ {movie.voteAverage ?? "-"}
              </span>
              <span className={styles.badge}>
                📅 {movie.releaseDate ?? "-"}
              </span>
              <span className={styles.badge}>
                🗣️ {movie.originalLanguage ?? "-"}
              </span>
              <span className={styles.badge}>
                👥 votes {movie.voteCount ?? "-"}
              </span>
            </div>

            {movie.overview && (
              <div className={styles.overview}>{movie.overview}</div>
            )}

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
        <h2>Your review to the movie</h2>
        <form className={styles.comment_form} onSubmit={onSubmit}>
          <textarea
            className={styles.comment_input}
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            placeholder="Write a comment..."
          />
          {!authLoading && !user ? (
            <div className={styles.comment_login_hint}>
              <a href="/login">Sign in</a> to add a comment.
            </div>
          ) : (
            <button
              type="submit"
              className={styles.comment_button}
              disabled={submitLoading || !commentContent.trim()}
            >
              Add
            </button>
          )}
        </form>
        <div className={styles.comments}>
          {(movie.comments ?? []).map((c) => (
            <CommentCard
              key={c.commentId}
              comment={c}
              isReplyOpen={replyToId === c.commentId}
              replyText={replyText}
              onReplyTextChange={setReplyText}
              onReplyOpen={() => openReply(c.commentId)}
              onReplyClose={closeReply}
              onReplySubmit={() => submitReply(c.commentId)}
              disabled={submitLoading}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
