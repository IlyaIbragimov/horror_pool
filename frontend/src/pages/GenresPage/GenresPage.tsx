import { useEffect, useState } from "react";
import { fetchGenres } from "../../api/genre.api";
import type { GenreAllResponse } from "../../types/genre.types";
import { GenreCard } from "../../components/GenreCard/GenreCard";
import { Pager } from "../../components/Pager/Pager";
import styles from "./GenresPage.module.css";

export function GenresPage() {
  const [page, setPage] = useState(1);
  const [size] = useState(18);

  const [data, setData] = useState<GenreAllResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
 

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetchGenres({ page: page - 1, size, order: "asc", sort: "name" })
      .then(setData)
      .catch((e) => setError(e instanceof Error ? e.message : String(e)))
      .finally(() => setLoading(false));
  }, [page, size]);

  return (
    <div className={styles.page}>
      <div className={styles.header}>

        <Pager
          className={styles.pager}
          loading={loading}
          page={page}
          pageNumber={data ? data.pageNumber + 1 : undefined}
          totalPages={data?.totalPages}
          lastPage={data?.lastPage}
          onPageChange={setPage}
        />
      </div>

      {error && <div className={styles.error}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div className={styles.list}>
        {data?.genres.map((g) => (
          <GenreCard key={g.genreId} genre={g} />
        ))}
      </div>
    </div>
  );
}
