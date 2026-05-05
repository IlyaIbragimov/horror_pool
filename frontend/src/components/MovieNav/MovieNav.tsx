import { useEffect, useRef, useState } from "react";
import styles from "./MovieNav.module.css";

export type MovieSortParam =
  | "title"
  | "popularity"
  | "releaseDate"
  | "voteAverage";

type MovieNavProps = {
  sortParam: MovieSortParam;
  orderParam: "asc" | "desc";
  yearParam?: number;
  onQueryChange: (patch: Record<string, string | null>) => void;
};

const currentYear = new Date().getFullYear();
const releaseYears = Array.from(
  { length: currentYear - 1899 },
  (_, index) => currentYear - index,
);

export function MovieNav({
  sortParam,
  orderParam,
  yearParam,
  onQueryChange,
}: MovieNavProps) {
  const [releaseYearMenuOpen, setReleaseYearMenuOpen] = useState(false);
  const releaseYearMenuRef = useRef<HTMLDivElement | null>(null);

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

  const setSort = (sort: MovieSortParam) => {
    onQueryChange({ sort, order: "desc", page: "1" });
  };

  const setYear = (year: string | null) => {
    onQueryChange({ year, page: "1" });
    setReleaseYearMenuOpen(false);
  };

  return (
    <nav className={styles.nav}>
      <button
        className={`${styles.navItem} ${sortParam === "releaseDate" && orderParam === "desc" ? styles.active : ""}`}
        onClick={() => setSort("releaseDate")}
        type="button"
      >
        Newest
      </button>

      <button
        className={`${styles.navItem} ${sortParam === "popularity" && orderParam === "desc" ? styles.active : ""}`}
        onClick={() => setSort("popularity")}
        type="button"
      >
        Popular
      </button>

      <button
        className={`${styles.navItem} ${sortParam === "voteAverage" && orderParam === "desc" ? styles.active : ""}`}
        onClick={() => setSort("voteAverage")}
        type="button"
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
        {releaseYearMenuOpen && (
          <div className={styles.releaseYearDropdown}>
            <button
              className={styles.releaseYearOption}
              onClick={() => setYear(null)}
              type="button"
            >
              All years
            </button>
            {releaseYears.map((year) => (
              <button
                className={`${styles.releaseYearOption} ${yearParam === year ? styles.activeYear : ""}`}
                key={year}
                onClick={() => setYear(String(year))}
                type="button"
              >
                {year}
              </button>
            ))}
          </div>
        )}
      </div>
    </nav>
  );
}
