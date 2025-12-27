
import { useEffect, useState } from "react";
import type { MovieDTO, MovieAllResponse } from "../api/movie.types";
import { fetchMovies } from "../api/movie.api";

export function MoviesPage() {
  const [data, setData] = useState<MovieAllResponse | null>(null);
  const [movies, setMovies] = useState<MovieDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const pageSize = 20;

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);

      try {
        const res = await fetchMovies({ page, size: pageSize, sort: "title", order: "asc" });
        if (cancelled) return;

        setData(res);
        setMovies(res.movies ?? []);
      } catch (e) {
        if (cancelled) return;
        setError(e instanceof Error ? e.message : "Unknown error");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [page]);

  const isFirstPage = (data?.pageNumber ?? page) <= 0;
  const isLastPage = data?.lastPage ?? false;

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, sans-serif" }}>
      <h1>Movies</h1>

      <div style={{ marginBottom: 12 }}>
        <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={loading || isFirstPage}>
          Prev
        </button>
        <span style={{ margin: "0 12px" }}>
          Page: {data?.pageNumber ?? page} / {data?.totalPages ?? "?"}
        </span>
        <button onClick={() => setPage((p) => p + 1)} disabled={loading || isLastPage}>
          Next
        </button>
      </div>

      {loading && <p>Loading...</p>}
      {error && <p style={{ color: "crimson" }}>{error}</p>}

      {!loading && !error && movies.length === 0 && <p>No movies yet.</p>}

      <ul style={{ display: "grid", gap: 12, padding: 0, listStyle: "none" }}>
        {movies.map((m) => (
          <li
            key={m.movieId ?? m.tmdbId ?? m.title}
            style={{
              border: "1px solid #ddd",
              borderRadius: 12,
              padding: 12,
            }}
          >
            <div style={{ fontWeight: 700 }}>
              {m.title ?? "(no title)"} {m.releaseYear ? `(${m.releaseYear})` : ""}
            </div>

            {m.originalTitle && m.originalTitle !== m.title && (
              <div style={{ opacity: 0.7, fontSize: 14 }}>{m.originalTitle}</div>
            )}

            {m.overview && <p style={{ marginTop: 8 }}>{m.overview}</p>}

            <div style={{ opacity: 0.7, fontSize: 14 }}>
              {m.releaseDate ? `Release: ${m.releaseDate}` : ""}
              {m.voteAverage != null ? ` • Rating: ${m.voteAverage}` : ""}
              {m.voteCount != null ? ` (${m.voteCount})` : ""}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}