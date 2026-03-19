import { useEffect, useState } from "react";
import {
  getAllUserWatchlists,
  createWatchlist,
  addMovieToWatchlist,
} from "../../api/watchlist.api";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
import { useNavigate, useLocation } from "react-router-dom";
import styles from "./AddWatchlistPage.module.css";

export function AddWatchlistPage() {
  const [size] = useState(4);

  const [page, setPage] = useState(1);
  const [data, setData] = useState<WatchlistAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const location = useLocation();
  const navigate = useNavigate();

  const state = location.state as {
    backgroundLocation?: Location;
    movieId?: number;
  } | null;

  const bg = state?.backgroundLocation;
  const movieId = state?.movieId;

  const close = () => {
    if (bg) navigate(bg.pathname + bg.search, { replace: true });
    else navigate("/movies", { replace: true });
  };

  const onOverlayMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) close();
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Escape") close();
  };

  const refresh = () => {
    setLoading(true);
    setError(null);
    getAllUserWatchlists({ page: page - 1, size, order: "asc" })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    refresh();
  }, [page, size]);

  useEffect(() => {
    if (!successMessage) return;

    const timeoutId = window.setTimeout(() => {
      setSuccessMessage(null);
    }, 2500);

    return () => window.clearTimeout(timeoutId);
  }, [successMessage]);

  const [watchlist_title, setWatchlistTitle] = useState("");
  const [isPublic, setPublic] = useState(true);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      await createWatchlist(watchlist_title, isPublic);
      await refresh();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Watchlist creation failed");
    } finally {
      setLoading(false);
    }
  };

  const handleAddMovie = async (watchlistId: number) => {
    if (!movieId) return;

    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      await addMovieToWatchlist(watchlistId, movieId);
      await refresh();
      const watchlistTitle =
        data?.watchlistDTOS.find((w) => w.watchlistId === watchlistId)?.title ??
        "watchlist";
      setSuccessMessage(`Movie added to "${watchlistTitle}"`);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Adding movie failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className={styles.overlay}
      onMouseDown={onOverlayMouseDown}
      onKeyDown={onKeyDown}
      aria-modal="true"
      role="dialog"
    >
      <div className={styles.modal}>
        {successMessage && (
          <div className={styles.successToast} role="status" aria-live="polite">
            {successMessage}
          </div>
        )}
        <div className={styles.header}>
          <h1 className={styles.title}>Add to watchlist...</h1>
          <button
            className={styles.closeBtn}
            onClick={close}
            type="button"
            aria-label="Close"
          >
            ✕
          </button>
        </div>
        <section className={styles.section}>
          <div className={styles.pager}>
            <button
              disabled={loading || page <= 1}
              onClick={() => setPage((p) => p - 1)}
            >
              Prev
            </button>
            <span>
              Page {data ? data.pageNumber + 1 : page} /{" "}
              {data?.totalPages ?? "?"}
            </span>
            <button
              disabled={loading || !data || data.lastPage}
              onClick={() => setPage((p) => p + 1)}
            >
              Next
            </button>
          </div>

          <div className={styles.list}>
            <div className={styles.new_watchlist}>
              <form className={styles.new_watchlist_form} onSubmit={onSubmit}>
                <input
                  className={styles.input}
                  value={watchlist_title}
                  onChange={(e) => setWatchlistTitle(e.target.value)}
                  placeholder="Watchlist title"
                  autoComplete="title"
                />

                <button
                  type="button"
                  className={styles.watchlistItem_isWatched_btn}
                  onClick={() => setPublic((prev) => !prev)}
                >
                  {isPublic ? "Public" : "Private"}
                </button>

                <button
                  className={styles.submit}
                  disabled={loading}
                  type="submit"
                >
                  {loading ? "Creating..." : "Create"}
                </button>
              </form>
            </div>

            {error && <div className={styles.error}>{error}</div>}
            {loading && <div>Loading...</div>}

            <div className={styles.user_watchlist}>
              {data?.watchlistDTOS.map((w) => (
                <div className={styles.card} key={w.watchlistId}>
                  <WatchlistCard
                    watchlist={w}
                    onChanged={() => {
                      refresh();
                    }}
                  />
                  <button
                    className={styles.add_btn}
                    onClick={() => handleAddMovie(w.watchlistId)}
                  >
                    +
                  </button>
                </div>
              ))}
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
