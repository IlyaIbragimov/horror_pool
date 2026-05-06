alter table movie
    add column if not exists trailer_url varchar(255);
