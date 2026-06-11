import { useCallback } from "react";
import { Link, useParams, useSearchParams } from "react-router-dom";
import { fetchMoviesByGenre } from "../../api/movie.api";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import { MovieNav } from "../../components/MovieNav/MovieNav";
import { Pager } from "../../components/Pager/Pager";
import { useAsyncResource } from "../../hooks/useAsyncResource";
import type { MovieAllResponse } from "../../types/movie.types";
import styles from "../MoviesPage/MoviesPage.module.css";

export function GenrePage() {
  const { genreId } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const page = Number(searchParams.get("page") ?? "1");
  const pageParam = Number(searchParams.get("page") ?? "1");
  const sizeParam = Number(searchParams.get("size") ?? "18");
  const sortParam = (searchParams.get("sort") ?? "title") as
    | "title"
    | "popularity"
    | "releaseDate"
    | "voteAverage";
  const orderParam = (searchParams.get("order") ?? "asc") as "asc" | "desc";
  const yearParamRaw = searchParams.get("year");
  const parsedYearParam = yearParamRaw ? Number(yearParamRaw) : undefined;
  const yearParam =
    parsedYearParam !== undefined && Number.isFinite(parsedYearParam)
      ? parsedYearParam
      : undefined;

  const setQuery = (patch: Record<string, string | null>) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(patch).forEach(([k, v]) => {
      if (v === null) {
        next.delete(k);
      } else {
        next.set(k, v);
      }
    });
    setSearchParams(next);
  };

  const goToPage = (p: number) => setQuery({ page: String(p) });

  const loadMovies = useCallback((): Promise<MovieAllResponse> => {
    const parsedGenreId = Number(genreId);

    if (!Number.isFinite(parsedGenreId)) {
      return Promise.reject(new Error("Invalid genre id."));
    }

    return fetchMoviesByGenre(parsedGenreId, {
      page: pageParam - 1,
      size: sizeParam,
      sort: sortParam,
      order: orderParam,
      year: yearParam,
    });
  }, [genreId, pageParam, sizeParam, sortParam, orderParam, yearParam]);

  const { data, loading, error } = useAsyncResource(loadMovies);

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <MovieNav
          sortParam={sortParam}
          orderParam={orderParam}
          yearParam={yearParam}
          onQueryChange={setQuery}
        />

        <Pager
          className={styles.pager}
          loading={loading}
          page={page}
          pageNumber={data ? data.pageNumber + 1 : undefined}
          totalPages={data?.totalPages}
          lastPage={data?.lastPage}
          onPageChange={goToPage}
        />
      </div>

      {error && <div className={styles.error}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div className={styles.grid}>
        {data?.movies.map((movie) => (
          <Link
            key={movie.movieId}
            to={`/movies/${movie.movieId}`}
            className={styles.cardLink}
          >
            <MovieCard movie={movie} />
          </Link>
        ))}
      </div>
    </div>
  );
}
