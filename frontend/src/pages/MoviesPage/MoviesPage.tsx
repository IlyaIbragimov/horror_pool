import { useCallback } from "react";
import { fetchMovies, searchMovie } from "../../api/movie.api";
import { getCachedMovies, setCachedMovies } from "../../cache/moviesCache";
import type { MovieAllResponse } from "../../types/movie.types";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import { MovieNav } from "../../components/MovieNav/MovieNav";
import { Pager } from "../../components/Pager/Pager";
import { useAsyncResource } from "../../hooks/useAsyncResource";
import styles from "./MoviesPage.module.css";
import { Link, useSearchParams } from "react-router-dom";

export function MoviesPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const page = Number(searchParams.get("page") ?? "1");
  const keyword = searchParams.get("keyword") ?? undefined;
  const pageParam = Number(searchParams.get("page") ?? "1");
  const sizeParam = Number(searchParams.get("size") ?? "18");
  const sortParam = (searchParams.get("sort") ?? "title") as "title" | "popularity" | "releaseDate" | "voteAverage";
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
    const paramsBase = { page: pageParam - 1, size: sizeParam, sort: sortParam, order: orderParam };
    const cacheParams = { ...paramsBase, keyword, year: yearParam };
    const cachedData = getCachedMovies(cacheParams);

    if (cachedData) {
      return Promise.resolve(cachedData);
    }

    const promise = keyword !== undefined || yearParam !== undefined
      ? searchMovie({ ...paramsBase, keyword: keyword ?? "", year: yearParam })
      : fetchMovies(paramsBase);

    return promise
      .then((result) => {
        setCachedMovies(cacheParams, result);
        return result;
      })
  }, [keyword, pageParam, sizeParam, sortParam, orderParam, yearParam]);

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
        {data?.movies.map((m) => (
         <Link key={m.movieId} to={`/movies/${m.movieId}`} className={styles.cardLink}>
            <MovieCard movie={m} />
         </Link>
        ))}
      </div>
    </div>
  );
}
