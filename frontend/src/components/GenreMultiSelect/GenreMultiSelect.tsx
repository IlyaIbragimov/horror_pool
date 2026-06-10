import { useEffect, useMemo, useState } from "react";
import { fetchGenreOptions } from "../../api/genre.api";
import type { GenreOption } from "../../types/genre.types";
import styles from "./GenreMultiSelect.module.css";

type Props = {
  selectedIds: number[];
  onChange: (selectedIds: number[]) => void;
  disabled?: boolean;
};

export function GenreMultiSelect({
  selectedIds,
  onChange,
  disabled = false,
}: Props) {
  const [options, setOptions] = useState<GenreOption[]>([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;

    fetchGenreOptions()
      .then((data) => {
        if (active) setOptions(data);
      })
      .catch((e) => {
        if (active) {
          setError(e instanceof Error ? e.message : "Could not load genres.");
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const selectedSet = useMemo(() => new Set(selectedIds), [selectedIds]);
  const selectedOptions = options.filter((option) =>
    selectedSet.has(option.genreId),
  );
  const filteredOptions = options.filter((option) =>
    option.name.toLowerCase().includes(query.trim().toLowerCase()),
  );

  const toggleGenre = (genreId: number) => {
    if (selectedSet.has(genreId)) {
      onChange(selectedIds.filter((id) => id !== genreId));
      return;
    }

    onChange([...selectedIds, genreId]);
  };

  return (
    <div className={styles.control}>
      <div className={styles.selected} aria-label="Selected genres">
        {selectedOptions.length === 0 && (
          <span className={styles.empty}>No genres selected</span>
        )}
        {selectedOptions.map((option) => (
          <button
            key={option.genreId}
            type="button"
            className={styles.chip}
            onClick={() => toggleGenre(option.genreId)}
            disabled={disabled}
            aria-label={`Remove ${option.name}`}
          >
            {option.name}
            <span aria-hidden="true">x</span>
          </button>
        ))}
      </div>

      <input
        className={styles.search}
        type="search"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search genres"
        disabled={disabled || loading}
        aria-label="Search genres"
      />

      <div className={styles.options}>
        {loading && <span className={styles.status}>Loading genres...</span>}
        {error && <span className={styles.error}>{error}</span>}
        {!loading &&
          !error &&
          filteredOptions.map((option) => (
            <label key={option.genreId} className={styles.option}>
              <input
                type="checkbox"
                checked={selectedSet.has(option.genreId)}
                onChange={() => toggleGenre(option.genreId)}
                disabled={disabled}
              />
              <span>{option.name}</span>
            </label>
          ))}
        {!loading && !error && filteredOptions.length === 0 && (
          <span className={styles.status}>No matching genres</span>
        )}
      </div>
    </div>
  );
}
