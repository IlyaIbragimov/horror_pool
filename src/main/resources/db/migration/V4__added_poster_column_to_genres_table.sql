ALTER TABLE genre
ADD COLUMN if not exists poster_path
VARCHAR(255);