import { useState, useEffect } from "react";
import type { WatchlistDTO } from "../../types/watchlist.types";
import { useAuth } from "../../auth/AuthContext";
import {
  followWatchlist,
  unfollowWatchlist,
  rateWatchlist,
} from "../../api/watchlist.api";
import { Link } from "react-router-dom";
import styles from "./WatchlistCard.module.css";
import { PencilIcon, SkullIcon } from "../Icons/icons";

type Props = {
  watchlist: WatchlistDTO;
  onChanged?: () => void;
  canRename?: boolean;
  onRename?: (title: string) => void;
};

const TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w342";

export function WatchlistCard({
  watchlist,
  onChanged,
  canRename,
  onRename,
}: Props) {
  const { user, loading } = useAuth();
  const [followLoading, setFollowLoading] = useState(false);
  const [rateLoading, setRateLoading] = useState(false);
  const watchlistItemsCount = watchlist.watchlistItemDTOS.length ?? 0;

  const avg = Math.max(0, Math.min(10, watchlist.rating ?? 0));
  const savedDisplay = Math.round(avg * 2) / 2;

  const [hoveredRating, setHoveredRating] = useState<number | null>(null);

  const display = hoveredRating ?? savedDisplay;

  const fillForIndex = (index: number) => {
    const diff = display - index;
    if (diff >= 1) return 1;
    if (diff >= 0.5) return 0.5;
    return 0;
  };

  const posters = watchlist.watchlistItemDTOS
    .map((i) => i.movieDTO.posterPath)
    .filter((p): p is string => Boolean(p))
    .slice(0, 7);

  const handleRate = async (rating: number) => {
    if (rateLoading || loading || !user) return;
    setRateLoading(true);
    try {
      await rateWatchlist(watchlist.watchlistId, rating);
      onChanged?.();
    } catch (e) {
      console.error(e);
    } finally {
      setRateLoading(false);
    }
  };

  const handleFollowToggle = async () => {
    if (followLoading) return;
    setFollowLoading(true);

    try {
      if (watchlist.followedByMe) {
        await unfollowWatchlist(watchlist.watchlistId);
      } else {
        await followWatchlist(watchlist.watchlistId);
      }

      onChanged?.();
    } catch (e) {
      console.error(e);
    } finally {
      setFollowLoading(false);
    }
  };

  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [draftTitle, setDraftTitle] = useState(watchlist.title);

  useEffect(() => {
    setDraftTitle(watchlist.title);
  }, [watchlist.title]);

  const handleTitleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const nextTitle = draftTitle.trim();
    if (!nextTitle || nextTitle === watchlist.title) {
      setIsEditingTitle(false);
      setDraftTitle(watchlist.title);
      return;
    }
    await onRename?.(nextTitle);
    setIsEditingTitle(false);
  };

  const openEdit = () => {
    setDraftTitle(watchlist.title);
    setIsEditingTitle(true);
  };

  const handleCancelOrSubmit = () => {
    setDraftTitle(watchlist.title);
    setIsEditingTitle(false);
  };

  return (
    <div className={styles.watchlist_card}>
      <Link
        className={styles.watchlist_posters}
        to={`/watchlist/${watchlist.watchlistId}`}
      >
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
      </Link>

      <div className={styles.watchlist_content}>
        {!isEditingTitle ? (
          <div className={styles.titleRow}>
            <Link to={`/watchlist/${watchlist.watchlistId}`}>
              <h3 className={styles.title}>{watchlist.title}</h3>
            </Link>
            {canRename && (
              <button
                type="button"
                onClick={openEdit}
                className={styles.renameBtn}
              >
                <PencilIcon className={styles.renameIcon} />
              </button>
            )}
          </div>
        ) : (
          <form onSubmit={handleTitleSubmit} className={styles.titleEditForm}>
            <input
              value={draftTitle}
              onChange={(e) => setDraftTitle(e.target.value)}
              onBlur={handleCancelOrSubmit}
              autoFocus
              className={styles.titleInput}
            />
          </form>
        )}

        <div className={styles.watchlist_total_items}>
          Containig {watchlistItemsCount} movies
        </div>

        <div className={styles.watchlist_rating}>
          Raiting: {watchlist.rating} Total {watchlist.rateCount} users have
          voted
        </div>

        <div className={styles.watchlist_rate}>
          <p>Rate watchlist:</p>
          <ul
            className={styles.rate_ul}
            onMouseLeave={() => setHoveredRating(null)}
          >
            {Array.from({ length: 10 }, (_, i) => {
              const ratingValue = i + 1;
              const fill = fillForIndex(i);
              return (
                <li key={ratingValue} className={styles.rate_item}>
                  <button
                    type="button"
                    className={styles.rate_btn}
                    onClick={() => handleRate(ratingValue)}
                    onMouseEnter={() => setHoveredRating(ratingValue)}
                    onMouseLeave={() => setHoveredRating(null)}
                  >
                    <span className={styles.iconWrap}>
                      <SkullIcon className={styles.iconEmpty} />{" "}
                      <span
                        className={styles.iconFill}
                        style={{ width: `${fill * 100}%` }}
                      >
                        <SkullIcon className={styles.iconFull} />
                      </span>
                    </span>
                  </button>
                </li>
              );
            })}
          </ul>
        </div>

        {!loading && user && !watchlist.ownedByMe && (
          <div className={styles.watchlist_actions}>
            <button
              type="button"
              className={styles.watchlist_action_btn}
              onClick={handleFollowToggle}
              disabled={followLoading}
            >
              {followLoading
                ? "Loading..."
                : watchlist.followedByMe
                  ? "Unfollow"
                  : "Follow"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
