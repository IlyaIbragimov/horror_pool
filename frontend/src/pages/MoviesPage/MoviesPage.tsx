import { useEffect, useRef, useState } from "react";
import { fetchMovies, searchMovie } from "../../api/movie.api";
import { getCachedMovies, setCachedMovies } from "../../cache/moviesCache";
import type { MovieAllResponse } from "../../types/movie.types";
import { MovieCard } from "../../components/MovieCard/MovieCard";
import { Pager } from "../../components/Pager/Pager";
import styles from "./MoviesPage.module.css";
import { Link, useSearchParams } from "react-router-dom";

const currentYear = new Date().getFullYear();
const releaseYears = Array.from(
  { length: currentYear - 1899 },
  (_, index) => currentYear - index,
);

export function MoviesPage() {
  
  const [data, setData] = useState<MovieAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [releaseYearMenuOpen, setReleaseYearMenuOpen] = useState(false);
  const releaseYearMenuRef = useRef<HTMLDivElement | null>(null);

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

  useEffect(() => {
    if (!releaseYearMenuOpen) return;

    const handlePointerDown = (event: PointerEvent) => {
      if (
        releaseYearMenuRef.current &&
        !releaseYearMenuRef.current.contains(event.target as Node)
      ) {
        setReleaseYearMenuOpen(false);
      }
    };

    document.addEventListener("pointerdown", handlePointerDown);

    return () => {
      document.removeEventListener("pointerdown", handlePointerDown);
    };
  }, [releaseYearMenuOpen]);

  useEffect(() => {
    const paramsBase = { page: pageParam - 1, size: sizeParam, sort: sortParam, order: orderParam };
    const cacheParams = { ...paramsBase, keyword, year: yearParam };
    const cachedData = getCachedMovies(cacheParams);

    if (cachedData) {
      setData(cachedData);
      setError(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    const promise = keyword !== undefined || yearParam !== undefined
      ? searchMovie({ ...paramsBase, keyword: keyword ?? "", year: yearParam })
      : fetchMovies(paramsBase);

    promise
      .then((result) => {
        setCachedMovies(cacheParams, result);
        setData(result);
      })
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [keyword, pageParam, sizeParam, sortParam, orderParam, yearParam]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>

        <nav className={styles.nav}>
        <button
            className={`${styles.navItem} ${sortParam === "releaseDate" && orderParam === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "releaseDate", order: "desc", page: "1" })}
            >
            Newest
        </button>

        <button
            className={`${styles.navItem} ${sortParam === "popularity" && orderParam === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "popularity", order: "desc", page: "1" })}
            >
           Popular
        </button>

        <button
            className={`${styles.navItem} ${sortParam === "voteAverage" && orderParam === "desc" ? styles.active : ""}`}
            onClick={() => setQuery({ sort: "voteAverage", order: "desc", page: "1" })}
            >
           Most Rated
        </button>

        <div className={styles.releaseYearMenu} ref={releaseYearMenuRef}>
          <button
            className={`${styles.navItem} ${yearParam ? styles.active : ""}`}
            onClick={() => setReleaseYearMenuOpen((open) => !open)}
            type="button"
          >
            Release Year{yearParam ? `: ${yearParam}` : ""}
          </button>
          {releaseYearMenuOpen && <div className={styles.releaseYearDropdown}>
            <button
              className={styles.releaseYearOption}
              onClick={() => {
                setQuery({ year: null, page: "1" });
                setReleaseYearMenuOpen(false);
              }}
              type="button"
            >
              All years
            </button>
            {releaseYears.map((year) => (
              <button
                className={`${styles.releaseYearOption} ${yearParam === year ? styles.activeYear : ""}`}
                key={year}
                onClick={() => {
                  setQuery({ year: String(year), page: "1" });
                  setReleaseYearMenuOpen(false);
                }}
                type="button"
              >
                {year}
              </button>
            ))}
          </div>}
        </div>
        </nav>

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
