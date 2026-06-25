UPDATE public.watchlist
SET rating = 0.0
WHERE rating IS NULL;

ALTER TABLE public.watchlist
  ALTER COLUMN rating SET DEFAULT 0.0,
  ALTER COLUMN rating SET NOT NULL;