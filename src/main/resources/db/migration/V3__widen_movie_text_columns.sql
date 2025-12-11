
ALTER TABLE public.movie
  ALTER COLUMN overview       TYPE text,
  ALTER COLUMN description    TYPE text;


ALTER TABLE public.movie
  ALTER COLUMN title          TYPE varchar(512),
  ALTER COLUMN original_title TYPE varchar(512),
  ALTER COLUMN poster_path    TYPE varchar(512),
  ALTER COLUMN backdrop_path  TYPE varchar(512);