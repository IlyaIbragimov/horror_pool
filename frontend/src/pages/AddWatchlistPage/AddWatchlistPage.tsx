import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  getAllUserWatchlists,
  createWatchlist,
  addMovieToWatchlist,
} from "../../api/watchlist.api";
import { invalidatePublicWatchlistsCache } from "../../cache/publicWatchlistsCache";
import { invalidateUserWatchlists } from "../../cache/userWatchlistsInvalidation";
import { ModalShell } from "../../components/ModalShell/ModalShell";
import { Pager } from "../../components/Pager/Pager";
import { WatchlistCard } from "../../components/WatchlistCard/WatchlistCard";
import type { WatchlistAllResponse } from "../../types/watchlist.types";
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
      invalidateUserWatchlists();
      if (isPublic) {
        invalidatePublicWatchlistsCache();
      }
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
      invalidateUserWatchlists();
      invalidatePublicWatchlistsCache();
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
    <ModalShell
      title="Add to watchlist..."
      onClose={close}
      overlayClassName={styles.overlay}
      modalClassName={styles.modal}
      headerClassName={styles.header}
      titleClassName={styles.title}
      closeButtonClassName={styles.closeBtn}
      beforeHeader={
        successMessage && (
          <div className={styles.successToast} role="status" aria-live="polite">
            {successMessage}
          </div>
        )
      }
    >
      <section className={styles.section}>
        <Pager
          className={styles.pager}
          loading={loading}
          page={page}
          pageNumber={data ? data.pageNumber + 1 : undefined}
          totalPages={data?.totalPages}
          lastPage={data?.lastPage}
          onPageChange={setPage}
        />

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

              <button className={styles.submit} disabled={loading} type="submit">
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
    </ModalShell>
  );
}
