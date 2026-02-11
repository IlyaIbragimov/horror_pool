ALTER TABLE public.watchlist
  ALTER COLUMN rating TYPE double precision USING rating::double precision,
  ADD COLUMN IF NOT EXISTS rate_count integer NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS public.watchlist_raters (
  watchlist_id bigint NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT watchlist_raters_pkey PRIMARY KEY (watchlist_id, user_id),
  CONSTRAINT fk_watchlist_raters_watchlist
    FOREIGN KEY (watchlist_id) REFERENCES public.watchlist (watchlist_id) ON DELETE CASCADE,
  CONSTRAINT fk_watchlist_raters_user
    FOREIGN KEY (user_id) REFERENCES public.users (user_id) ON DELETE CASCADE
);
