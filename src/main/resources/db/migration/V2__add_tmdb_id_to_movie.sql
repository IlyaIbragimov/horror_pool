ALTER TABLE movie
    add column if not exists tmdb_id
    BIGINT;

    DO $$
   BEGIN
     IF NOT EXISTS (
     SELECT 1
     FROM pg_constraint
     where conname = 'uq_movie_tmdb_id'
     ) THEN
     ALTER TABLE movie ADD
   CONSTRAINT uq_movie_tmdb_id UNIQUE (tmdb_id) ;
     END IF;
   END $$;


