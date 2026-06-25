CREATE UNIQUE INDEX IF NOT EXISTS uq_movie_title_release_date_ci
ON public.movie (lower(title), release_date);