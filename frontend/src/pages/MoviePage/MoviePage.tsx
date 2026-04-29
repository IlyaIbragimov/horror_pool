import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type { AdminMovieDTO, CommentNode } from "../../types/movie.types";
import type {
  AdminMovieFormState,
  AdminMoviePayload,
} from "../../types/admin.types";
import {
  fetchMovieById,
  addCommentToMovie,
  replyToComment,
  editComment,
  deleteComment,
} from "../../api/movie.api";
import { invalidateMoviesCache } from "../../cache/moviesCache";
import { invalidatePublicWatchlistsCache } from "../../cache/publicWatchlistsCache";
import { invalidateUserWatchlists } from "../../cache/userWatchlistsInvalidation";
import { deleteMovie, editMovie } from "../../api/admin.api";
import styles from "./MoviePage.module.css";
import { CommentCard } from "../../components/CommentCard/CommentCard";
import { useAuth } from "../../auth/AuthContext";
import { buildCommentsTree } from "../../mappers/CommentTreeMapper";
import { useNavigate, useLocation } from "react-router-dom";

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w500";

function parseOptionalNumber(value: string): number | null | undefined {
  const trimmed = value.trim();
  if (!trimmed) return undefined;
  const parsed = Number(trimmed);
  return Number.isNaN(parsed) ? null : parsed;
}

function parseOptionalBoolean(value: string): boolean | null | undefined {
  const trimmed = value.trim().toLowerCase();
  if (!trimmed) return undefined;
  if (trimmed === "true") return true;
  if (trimmed === "false") return false;
  return null;
}

function buildInitialEditForm(movie: AdminMovieDTO): AdminMovieFormState {
  return {
    title: movie.title ?? "",
    originalTitle: movie.originalTitle ?? "",
    description: movie.description ?? "",
    overview: movie.overview ?? "",
    releaseDate: movie.releaseDate ?? "",
    releaseYear:
      movie.releaseYear === null || movie.releaseYear === undefined
        ? ""
        : String(movie.releaseYear),
    posterPath: movie.posterPath ?? "",
    backdropPath: movie.backdropPath ?? "",
    voteAverage:
      movie.voteAverage === null || movie.voteAverage === undefined
        ? ""
        : String(movie.voteAverage),
    voteCount:
      movie.voteCount === null || movie.voteCount === undefined
        ? ""
        : String(movie.voteCount),
    popularity:
      movie.popularity === null || movie.popularity === undefined
        ? ""
        : String(movie.popularity),
    originalLanguage: movie.originalLanguage ?? "",
    adult:
      movie.adult === null || movie.adult === undefined
        ? ""
        : String(movie.adult),
    video:
      movie.video === null || movie.video === undefined
        ? ""
        : String(movie.video),
    genreIds: movie.genres?.map((genre) => genre.genreId).join(", ") ?? "",
  };
}

export function MoviePage() {
  const { movieId } = useParams();

  const [movie, setMovie] = useState<AdminMovieDTO | null>(null);
  const [pageLoading, setPageLoading] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [commentContent, setCommentContent] = useState("");
  const { user, isAdmin, loading: authLoading } = useAuth();
  const [activeForm, setActiveForm] = useState<{
    type: "reply" | "edit";
    commentId: number;
  } | null>(null);
  const [formText, setFormText] = useState("");
  const [showMovieEditForm, setShowMovieEditForm] = useState(false);
  const [movieActionLoading, setMovieActionLoading] = useState(false);
  const [movieEditForm, setMovieEditForm] =
    useState<AdminMovieFormState | null>(null);
  const navigate = useNavigate();
  const location = useLocation();

  const openReply = (commentId: number) => {
    if (!user) {
      navigate("/login", { state: { backgroundLocation: location } });
      return;
    }
    setActiveForm({ type: "reply", commentId });
    setFormText("");
  };

  const openEdit = (commentId: number, commentContent: string) => {
    if (!user) {
      navigate("/login", { state: { backgroundLocation: location } });
      return;
    }
    setActiveForm({ type: "edit", commentId });
    setFormText(commentContent);
  };

  const closeForm = () => {
    setActiveForm(null);
    setFormText("");
  };

  const submitReply = async (parentCommentId: number) => {
    if (!movieId) return;
    if (!user) {
      navigate("/login", { state: { backgroundLocation: location } });
      return;
    }
    const text = formText.trim();
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
      closeForm();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Reply failed");
    } finally {
      setSubmitLoading(false);
    }
  };

  const submitEdit = async (commentId: number) => {
    if (!movieId) return;
    if (!user) {
      navigate("/login", { state: { backgroundLocation: location } });
      return;
    }
    const text = formText.trim();
    if (!text) return;
    setSubmitLoading(true);
    setError(null);
    try {
      const updatedMovie = await editComment(Number(movieId), commentId, text);
      setMovie(updatedMovie);
      closeForm();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Edit failed");
    } finally {
      setSubmitLoading(false);
    }
  };

  const submitDeleteComment = async (commentId: number) => {
    if (!movieId) return;
    if (!user) {
      navigate("/login", { state: { backgroundLocation: location } });
      return;
    }
    setSubmitLoading(true);
    setError(null);
    try {
      const updatedMovie = await deleteComment(Number(movieId), commentId);
      setMovie(updatedMovie);
      closeForm();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Delete failed");
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
      .then((data) => {
        setMovie(data);
        setMovieEditForm(buildInitialEditForm(data));
      })
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setPageLoading(false));
  }, [movieId]);

  const openMovieEditForm = () => {
    if (!movie) return;
    setMovieEditForm(buildInitialEditForm(movie));
    setShowMovieEditForm(true);
    setError(null);
  };

  const closeMovieEditForm = () => {
    if (!movie) {
      setShowMovieEditForm(false);
      return;
    }
    setMovieEditForm(buildInitialEditForm(movie));
    setShowMovieEditForm(false);
  };

  const handleMovieEditChange = (
    field: keyof AdminMovieFormState,
    value: string,
  ) => {
    setMovieEditForm((current) =>
      current ? { ...current, [field]: value } : current,
    );
  };

  const submitDeleteMovie = async () => {
    if (!movieId || !isAdmin) return;
    const numericMovieId = Number(movieId);
    if (Number.isNaN(numericMovieId)) return;
    if (!window.confirm("Delete this movie?")) return;

    setMovieActionLoading(true);
    setError(null);
    try {
      await deleteMovie(numericMovieId);
      invalidateMoviesCache();
      invalidatePublicWatchlistsCache();
      invalidateUserWatchlists();
      navigate("/movies", { replace: true });
    } catch (e) {
      setError(e instanceof Error ? e.message : "Delete movie failed");
    } finally {
      setMovieActionLoading(false);
    }
  };

  const submitMovieEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!movieId || !movieEditForm || !isAdmin) return;

    const numericMovieId = Number(movieId);
    if (Number.isNaN(numericMovieId)) return;

    const releaseYear = parseOptionalNumber(movieEditForm.releaseYear);
    const voteAverage = parseOptionalNumber(movieEditForm.voteAverage);
    const voteCount = parseOptionalNumber(movieEditForm.voteCount);
    const popularity = parseOptionalNumber(movieEditForm.popularity);
    const adult = parseOptionalBoolean(movieEditForm.adult);
    const video = parseOptionalBoolean(movieEditForm.video);

    if (
      releaseYear === null ||
      voteAverage === null ||
      voteCount === null ||
      popularity === null
    ) {
      setError("Numeric fields must contain valid numbers.");
      return;
    }

    if (adult === null || video === null) {
      setError('Boolean fields must be either "true" or "false".');
      return;
    }

    const genreIdValues = movieEditForm.genreIds.trim();
    const genreIds = !genreIdValues
      ? undefined
      : genreIdValues
          .split(",")
          .map((value) => Number(value.trim()))
          .filter((value) => !Number.isNaN(value));

    if (genreIdValues && (!genreIds || genreIds.length === 0)) {
      setError("Genre IDs must be comma-separated numbers.");
      return;
    }

    const payload: AdminMoviePayload = {
      title: movieEditForm.title.trim(),
      originalTitle: movieEditForm.originalTitle.trim() || undefined,
      description: movieEditForm.description.trim() || undefined,
      overview: movieEditForm.overview.trim() || undefined,
      releaseDate: movieEditForm.releaseDate || undefined,
      releaseYear: releaseYear ?? undefined,
      posterPath: movieEditForm.posterPath.trim() || undefined,
      backdropPath: movieEditForm.backdropPath.trim() || undefined,
      voteAverage: voteAverage ?? undefined,
      voteCount: voteCount ?? undefined,
      popularity: popularity ?? undefined,
      originalLanguage: movieEditForm.originalLanguage.trim() || undefined,
      adult: adult ?? undefined,
      video: video ?? undefined,
      genres: genreIds?.map((genreId) => ({
        genreId,
        name: "",
        description: null,
        posterPath: null,
      })),
    };

    setMovieActionLoading(true);
    setError(null);
    try {
      const updatedMovie = await editMovie(numericMovieId, payload);
      invalidateMoviesCache();
      invalidatePublicWatchlistsCache();
      invalidateUserWatchlists();
      setMovie(updatedMovie);
      setMovieEditForm(buildInitialEditForm(updatedMovie));
      setShowMovieEditForm(false);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Edit movie failed");
    } finally {
      setMovieActionLoading(false);
    }
  };

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

  const tree = buildCommentsTree(movie.comments);

  const renderNodes = (nodes: CommentNode[], depth = 0) => (
    <>
      {nodes.map((node) => {
        const isFormOpen = activeForm?.commentId === node.commentId;
        const isEditing = isFormOpen && activeForm?.type === "edit";
        const canEdit = !!user && user === node.userName;
        const handleSubmit = isEditing
          ? () => submitEdit(node.commentId)
          : () => submitReply(node.commentId);

        return (
          <div key={node.commentId}>
            <CommentCard
              comment={node}
              depth={depth}
              isFormOpen={isFormOpen}
              isEditing={isEditing}
              formText={formText}
              onFormTextChange={setFormText}
              onReplyOpen={() => openReply(node.commentId)}
              onEditOpen={
                canEdit
                  ? () => openEdit(node.commentId, node.commentContent)
                  : undefined
              }
              onDelete={
                canEdit
                  ? () => {
                      if (!window.confirm("Delete this comment?")) return;
                      submitDeleteComment(node.commentId);
                    }
                  : undefined
              }
              onFormClose={closeForm}
              onFormSubmit={handleSubmit}
              disabled={submitLoading}
              canEdit={canEdit}
            />
            {node.replies.length > 0 && renderNodes(node.replies, depth + 1)}
          </div>
        );
      })}
    </>
  );

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!movieId) return;
    if (!user) {
      navigate("/login", {
        state: { from: location.pathname + location.search },
      });
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

  const handleAddToWatchlistClick = (
  e: React.MouseEvent<HTMLAnchorElement>,
) => {
  if (authLoading) {
    e.preventDefault();
    return;
  }

  if (!user) {
    e.preventDefault();
    navigate("/login", { state: { backgroundLocation: location } });
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
                TMDB rating: ⭐ {movie.voteAverage ?? "-"}
              </span>
              <span className={styles.badge}>
                Release date: 📅 {movie.releaseDate ?? "-"}
              </span>
              <span className={styles.badge}>
                Original language: 🗣️ {movie.originalLanguage ?? "-"}
              </span>
              <span className={styles.badge}>
                TMDB votes: 👥 {movie.voteCount ?? "-"}
              </span>
            </div>

            {movie.overview && (
              <div className={styles.overview}>{movie.overview}</div>
            )}

            <div className={styles.movie_page_actions}>
              <Link
                className={styles.action_add}
                state={{
                  backgroundLocation: location,
                  movieId: Number(movieId),
                }}
                to="/addMovieToWatchlist"
                onClick={handleAddToWatchlistClick}
              >
                Add to watchlist
              </Link>
              {isAdmin && (
                <>
                  <button
                    type="button"
                    className={styles.action_edit}
                    onClick={openMovieEditForm}
                    disabled={movieActionLoading}
                  >
                    Edit movie
                  </button>
                  <button
                    type="button"
                    className={styles.action_delete}
                    onClick={submitDeleteMovie}
                    disabled={movieActionLoading}
                  >
                    Delete movie
                  </button>
                </>
              )}
            </div>

            {isAdmin && showMovieEditForm && movieEditForm && (
              <form
                className={styles.movie_edit_form}
                onSubmit={submitMovieEdit}
              >
                <div className={styles.movie_edit_grid}>
                  <label className={styles.movie_edit_field}>
                    <span>Title</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.title}
                      onChange={(e) =>
                        handleMovieEditChange("title", e.target.value)
                      }
                      required
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Original title</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.originalTitle}
                      onChange={(e) =>
                        handleMovieEditChange("originalTitle", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Release date</span>
                    <input
                      className={styles.movie_edit_input}
                      type="date"
                      value={movieEditForm.releaseDate}
                      onChange={(e) =>
                        handleMovieEditChange("releaseDate", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Release year</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.releaseYear}
                      onChange={(e) =>
                        handleMovieEditChange("releaseYear", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Poster path</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.posterPath}
                      onChange={(e) =>
                        handleMovieEditChange("posterPath", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Backdrop path</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.backdropPath}
                      onChange={(e) =>
                        handleMovieEditChange("backdropPath", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Vote average</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.voteAverage}
                      onChange={(e) =>
                        handleMovieEditChange("voteAverage", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Vote count</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.voteCount}
                      onChange={(e) =>
                        handleMovieEditChange("voteCount", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Popularity</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.popularity}
                      onChange={(e) =>
                        handleMovieEditChange("popularity", e.target.value)
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Original language</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.originalLanguage}
                      onChange={(e) =>
                        handleMovieEditChange(
                          "originalLanguage",
                          e.target.value,
                        )
                      }
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Adult</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.adult}
                      onChange={(e) =>
                        handleMovieEditChange("adult", e.target.value)
                      }
                      placeholder="true or false"
                    />
                  </label>

                  <label className={styles.movie_edit_field}>
                    <span>Video</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.video}
                      onChange={(e) =>
                        handleMovieEditChange("video", e.target.value)
                      }
                      placeholder="true or false"
                    />
                  </label>

                  <label
                    className={`${styles.movie_edit_field} ${styles.movie_edit_field_full}`}
                  >
                    <span>Overview</span>
                    <textarea
                      className={styles.movie_edit_textarea}
                      value={movieEditForm.overview}
                      onChange={(e) =>
                        handleMovieEditChange("overview", e.target.value)
                      }
                      rows={4}
                    />
                  </label>

                  <label
                    className={`${styles.movie_edit_field} ${styles.movie_edit_field_full}`}
                  >
                    <span>Description</span>
                    <textarea
                      className={styles.movie_edit_textarea}
                      value={movieEditForm.description}
                      onChange={(e) =>
                        handleMovieEditChange("description", e.target.value)
                      }
                      rows={5}
                    />
                  </label>

                  <label
                    className={`${styles.movie_edit_field} ${styles.movie_edit_field_full}`}
                  >
                    <span>Genre IDs</span>
                    <input
                      className={styles.movie_edit_input}
                      value={movieEditForm.genreIds}
                      onChange={(e) =>
                        handleMovieEditChange("genreIds", e.target.value)
                      }
                      placeholder="1, 2, 3"
                    />
                  </label>
                </div>

                <div className={styles.movie_edit_actions}>
                  <button
                    type="submit"
                    className={styles.movie_edit_submit}
                    disabled={movieActionLoading}
                  >
                    {movieActionLoading ? "Saving..." : "Save changes"}
                  </button>
                  <button
                    type="button"
                    className={styles.movie_edit_cancel}
                    onClick={closeMovieEditForm}
                    disabled={movieActionLoading}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            )}
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
              <Link state={{ backgroundLocation: location }} to="/login">
                Sign in
              </Link>{" "}
              to add a comment.
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
        <div className={styles.comments}>{renderNodes(tree)}</div>
      </div>
    </div>
  );
}
