CREATE TABLE IF NOT EXISTS public.watchlist_followers (
  watchlist_id bigint NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT watchlist_followers_pkey PRIMARY KEY (watchlist_id, user_id),
  CONSTRAINT fk_watchlist_followers_watchlist
    FOREIGN KEY (watchlist_id) REFERENCES public.watchlist (watchlist_id) ON DELETE CASCADE,
  CONSTRAINT fk_watchlist_followers_user
    FOREIGN KEY (user_id) REFERENCES public.users (user_id) ON DELETE CASCADE
);
