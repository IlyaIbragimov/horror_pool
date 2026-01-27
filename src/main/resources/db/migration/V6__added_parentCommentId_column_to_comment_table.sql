ALTER TABLE comment
  ADD COLUMN IF NOT EXISTS parent_comment_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_comments_movie_parent
  ON comment (movie_id, parent_comment_id);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_comments_parent'
  ) THEN
    ALTER TABLE comment
      ADD CONSTRAINT fk_comments_parent
      FOREIGN KEY (parent_comment_id)
      REFERENCES comment (comment_id)
      ON DELETE CASCADE;
  END IF;
END $$;