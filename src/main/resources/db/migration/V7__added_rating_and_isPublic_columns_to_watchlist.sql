ALTER TABLE public.watchlist
  ADD COLUMN IF NOT EXISTS rating bigint,
  ADD COLUMN IF NOT EXISTS is_public boolean NOT NULL DEFAULT false;