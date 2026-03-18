import { useState } from "react";
import {
  addGenre,
  addMovie,
  changeUserLockStatus,
  disableUser,
} from "../../api/admin.api";
import type {
  AdminGenrePayload,
  AdminMoviePayload,
} from "../../types/admin.types";
import styles from "./AdminPage.module.css";

type AdminPanel = "user" | "movie" | "genre";

type MovieFormState = {
  title: string;
  originalTitle: string;
  description: string;
  overview: string;
  releaseDate: string;
  releaseYear: string;
  posterPath: string;
  backdropPath: string;
  voteAverage: string;
  voteCount: string;
  popularity: string;
  originalLanguage: string;
  adult: string;
  video: string;
  genreIds: string;
};

type GenreFormState = {
  name: string;
  description: string;
  posterPath: string;
};

const initialMovieForm: MovieFormState = {
  title: "",
  originalTitle: "",
  description: "",
  overview: "",
  releaseDate: "",
  releaseYear: "",
  posterPath: "",
  backdropPath: "",
  voteAverage: "",
  voteCount: "",
  popularity: "",
  originalLanguage: "",
  adult: "",
  video: "",
  genreIds: "",
};

const initialGenreForm: GenreFormState = {
  name: "",
  description: "",
  posterPath: "",
};

function parseOptionalNumber(value: string): number | null | undefined {
  const trimmed = value.trim();
  if (!trimmed) return undefined;
  const parsed = Number(trimmed);
  return Number.isNaN(parsed) ? null : parsed;
}

function parseOptionalBoolean(value: string): boolean | null | undefined {
  const trimmed = value.trim().toLowerCase();
  if (!trimmed) return undefined;
  if (trimmed === "true") return true;
  if (trimmed === "false") return false;
  return null;
}

function parseGenreIds(value: string): AdminMoviePayload["genres"] | null {
  const trimmed = value.trim();
  if (!trimmed) return undefined;

  const ids = trimmed
    .split(",")
    .map((item) => Number(item.trim()))
    .filter((id) => !Number.isNaN(id));

  if (!ids.length) return null;

  return ids.map((genreId) => ({
    genreId,
    name: "",
    description: null,
    posterPath: null,
  }));
}

export function AdminPage() {
  const [activePanel, setActivePanel] = useState<AdminPanel>("user");

  const [userId, setUserId] = useState("");
  const [userLoading, setUserLoading] = useState(false);
  const [userMessage, setUserMessage] = useState<string | null>(null);
  const [userError, setUserError] = useState<string | null>(null);

  const [movieForm, setMovieForm] = useState<MovieFormState>(initialMovieForm);
  const [movieLoading, setMovieLoading] = useState(false);
  const [movieMessage, setMovieMessage] = useState<string | null>(null);
  const [movieError, setMovieError] = useState<string | null>(null);

  const [genreForm, setGenreForm] = useState<GenreFormState>(initialGenreForm);
  const [genreLoading, setGenreLoading] = useState(false);
  const [genreMessage, setGenreMessage] = useState<string | null>(null);
  const [genreError, setGenreError] = useState<string | null>(null);

  const handleUserAction = async (action: "lock" | "disable") => {
    const parsedUserId = Number(userId.trim());

    if (!userId.trim() || Number.isNaN(parsedUserId)) {
      setUserError("Please enter a valid user ID.");
      setUserMessage(null);
      return;
    }

    setUserLoading(true);
    setUserError(null);
    setUserMessage(null);

    try {
      const response =
        action === "lock"
          ? await changeUserLockStatus(parsedUserId)
          : await disableUser(parsedUserId);

      setUserMessage(
        action === "lock"
          ? `User ${response.username} lock status is now ${response.locked ? "locked" : "unlocked"}.`
          : `User ${response.username} enabled status is now ${response.enabled ? "enabled" : "disabled"}.`,
      );
    } catch (e) {
      setUserError(
        e instanceof Error ? e.message : "User action could not be completed.",
      );
    } finally {
      setUserLoading(false);
    }
  };

  const handleMovieChange = (
    field: keyof MovieFormState,
    value: string,
  ) => {
    setMovieForm((current) => ({ ...current, [field]: value }));
  };

  const handleGenreChange = (
    field: keyof GenreFormState,
    value: string,
  ) => {
    setGenreForm((current) => ({ ...current, [field]: value }));
  };

  const handleMovieSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMovieLoading(true);
    setMovieError(null);
    setMovieMessage(null);

    const releaseYear = parseOptionalNumber(movieForm.releaseYear);
    const voteAverage = parseOptionalNumber(movieForm.voteAverage);
    const voteCount = parseOptionalNumber(movieForm.voteCount);
    const popularity = parseOptionalNumber(movieForm.popularity);
    const adult = parseOptionalBoolean(movieForm.adult);
    const video = parseOptionalBoolean(movieForm.video);
    const genres = parseGenreIds(movieForm.genreIds);

    if (
      releaseYear === null ||
      voteAverage === null ||
      voteCount === null ||
      popularity === null
    ) {
      setMovieError("Numeric fields must contain valid numbers.");
      setMovieLoading(false);
      return;
    }

    if (adult === null || video === null) {
      setMovieError('Boolean fields must be either "true" or "false".');
      setMovieLoading(false);
      return;
    }

    if (genres === null) {
      setMovieError("Genre IDs must be comma-separated numbers.");
      setMovieLoading(false);
      return;
    }

    const payload: AdminMoviePayload = {
      title: movieForm.title.trim(),
      originalTitle: movieForm.originalTitle.trim() || undefined,
      description: movieForm.description.trim() || undefined,
      overview: movieForm.overview.trim() || undefined,
      releaseDate: movieForm.releaseDate || undefined,
      releaseYear: releaseYear ?? undefined,
      posterPath: movieForm.posterPath.trim() || undefined,
      backdropPath: movieForm.backdropPath.trim() || undefined,
      voteAverage: voteAverage ?? undefined,
      voteCount: voteCount ?? undefined,
      popularity: popularity ?? undefined,
      originalLanguage: movieForm.originalLanguage.trim() || undefined,
      adult: adult ?? undefined,
      video: video ?? undefined,
      genres,
    };

    try {
      const response = await addMovie(payload);
      setMovieMessage(`Movie "${response.title}" was created successfully.`);
      setMovieForm(initialMovieForm);
    } catch (e) {
      setMovieError(
        e instanceof Error ? e.message : "Movie could not be created.",
      );
    } finally {
      setMovieLoading(false);
    }
  };

  const handleGenreSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenreLoading(true);
    setGenreError(null);
    setGenreMessage(null);

    const payload: AdminGenrePayload = {
      name: genreForm.name.trim(),
      description: genreForm.description.trim(),
      posterPath: genreForm.posterPath.trim() || undefined,
    };

    try {
      const response = await addGenre(payload);
      setGenreMessage(`Genre "${response.name}" was created successfully.`);
      setGenreForm(initialGenreForm);
    } catch (e) {
      setGenreError(
        e instanceof Error ? e.message : "Genre could not be created.",
      );
    } finally {
      setGenreLoading(false);
    }
  };

  return (
    <section className={styles.page}>


      <div className={styles.panelShell}>
        <div className={styles.tabRow}>
          <button
            type="button"
            className={`${styles.tab} ${activePanel === "user" ? styles.tabActive : ""}`}
            onClick={() => setActivePanel("user")}
          >
            user panel
          </button>
          <button
            type="button"
            className={`${styles.tab} ${activePanel === "movie" ? styles.tabActive : ""}`}
            onClick={() => setActivePanel("movie")}
          >
            movie panel
          </button>
          <button
            type="button"
            className={`${styles.tab} ${activePanel === "genre" ? styles.tabActive : ""}`}
            onClick={() => setActivePanel("genre")}
          >
            genre panel
          </button>
        </div>

        {activePanel === "user" && (
          <div className={styles.panel}>
            <div className={styles.panelHeader}>
              <h2>User Panel</h2>
              <p>Toggle account lock or enabled status by user ID.</p>
            </div>

            <div className={styles.fieldGridSingle}>
              <label className={styles.field}>
                <span>User ID</span>
                <input
                  className={styles.input}
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                  placeholder="Enter user ID"
                />
              </label>
            </div>

            <div className={styles.actionRow}>
              <button
                type="button"
                className={styles.primaryButton}
                onClick={() => handleUserAction("lock")}
                disabled={userLoading}
              >
                {userLoading ? "Working..." : "Lock user"}
              </button>
              <button
                type="button"
                className={styles.secondaryButton}
                onClick={() => handleUserAction("disable")}
                disabled={userLoading}
              >
                {userLoading ? "Working..." : "Disable user"}
              </button>
            </div>

            {userMessage && <p className={styles.success}>{userMessage}</p>}
            {userError && <p className={styles.error}>{userError}</p>}
          </div>
        )}

        {activePanel === "movie" && (
          <form className={styles.panel} onSubmit={handleMovieSubmit}>
            <div className={styles.panelHeader}>
              <h2>Movie Panel</h2>
              <p>Create a new movie using the admin movie payload fields.</p>
            </div>

            <div className={styles.fieldGrid}>
              <label className={styles.field}>
                <span>Title</span>
                <input
                  className={styles.input}
                  value={movieForm.title}
                  onChange={(e) => handleMovieChange("title", e.target.value)}
                  placeholder="Movie title"
                  required
                />
              </label>

              <label className={styles.field}>
                <span>Original title</span>
                <input
                  className={styles.input}
                  value={movieForm.originalTitle}
                  onChange={(e) =>
                    handleMovieChange("originalTitle", e.target.value)
                  }
                  placeholder="Original title"
                />
              </label>

              <label className={styles.field}>
                <span>Release date</span>
                <input
                  className={styles.input}
                  type="date"
                  value={movieForm.releaseDate}
                  onChange={(e) =>
                    handleMovieChange("releaseDate", e.target.value)
                  }
                />
              </label>

              <label className={styles.field}>
                <span>Release year</span>
                <input
                  className={styles.input}
                  value={movieForm.releaseYear}
                  onChange={(e) =>
                    handleMovieChange("releaseYear", e.target.value)
                  }
                  placeholder="2024"
                />
              </label>

              <label className={styles.field}>
                <span>Poster path</span>
                <input
                  className={styles.input}
                  value={movieForm.posterPath}
                  onChange={(e) =>
                    handleMovieChange("posterPath", e.target.value)
                  }
                  placeholder="/poster.jpg"
                />
              </label>

              <label className={styles.field}>
                <span>Backdrop path</span>
                <input
                  className={styles.input}
                  value={movieForm.backdropPath}
                  onChange={(e) =>
                    handleMovieChange("backdropPath", e.target.value)
                  }
                  placeholder="/backdrop.jpg"
                />
              </label>

              <label className={styles.field}>
                <span>Vote average</span>
                <input
                  className={styles.input}
                  value={movieForm.voteAverage}
                  onChange={(e) =>
                    handleMovieChange("voteAverage", e.target.value)
                  }
                  placeholder="7.8"
                />
              </label>

              <label className={styles.field}>
                <span>Vote count</span>
                <input
                  className={styles.input}
                  value={movieForm.voteCount}
                  onChange={(e) =>
                    handleMovieChange("voteCount", e.target.value)
                  }
                  placeholder="1200"
                />
              </label>

              <label className={styles.field}>
                <span>Popularity</span>
                <input
                  className={styles.input}
                  value={movieForm.popularity}
                  onChange={(e) =>
                    handleMovieChange("popularity", e.target.value)
                  }
                  placeholder="45.4"
                />
              </label>

              <label className={styles.field}>
                <span>Original language</span>
                <input
                  className={styles.input}
                  value={movieForm.originalLanguage}
                  onChange={(e) =>
                    handleMovieChange("originalLanguage", e.target.value)
                  }
                  placeholder="en"
                />
              </label>

              <label className={styles.field}>
                <span>Adult</span>
                <input
                  className={styles.input}
                  value={movieForm.adult}
                  onChange={(e) => handleMovieChange("adult", e.target.value)}
                  placeholder="true or false"
                />
              </label>

              <label className={styles.field}>
                <span>Video</span>
                <input
                  className={styles.input}
                  value={movieForm.video}
                  onChange={(e) => handleMovieChange("video", e.target.value)}
                  placeholder="true or false"
                />
              </label>

              <label className={`${styles.field} ${styles.fieldFull}`}>
                <span>Overview</span>
                <textarea
                  className={styles.textarea}
                  value={movieForm.overview}
                  onChange={(e) => handleMovieChange("overview", e.target.value)}
                  placeholder="Short overview"
                  rows={4}
                />
              </label>

              <label className={`${styles.field} ${styles.fieldFull}`}>
                <span>Description</span>
                <textarea
                  className={styles.textarea}
                  value={movieForm.description}
                  onChange={(e) =>
                    handleMovieChange("description", e.target.value)
                  }
                  placeholder="Longer description"
                  rows={5}
                />
              </label>

              <label className={`${styles.field} ${styles.fieldFull}`}>
                <span>Genre IDs</span>
                <input
                  className={styles.input}
                  value={movieForm.genreIds}
                  onChange={(e) => handleMovieChange("genreIds", e.target.value)}
                  placeholder="1, 2, 3"
                />
              </label>
            </div>

            <div className={styles.actionRow}>
              <button
                className={styles.primaryButton}
                disabled={movieLoading}
                type="submit"
              >
                {movieLoading ? "Creating..." : "Create movie"}
              </button>
            </div>

            {movieMessage && <p className={styles.success}>{movieMessage}</p>}
            {movieError && <p className={styles.error}>{movieError}</p>}
          </form>
        )}

        {activePanel === "genre" && (
          <form className={styles.panel} onSubmit={handleGenreSubmit}>
            <div className={styles.panelHeader}>
              <h2>Genre Panel</h2>
              <p>Create a new genre with the same fields used by the backend.</p>
            </div>

            <div className={styles.fieldGridSingle}>
              <label className={styles.field}>
                <span>Name</span>
                <input
                  className={styles.input}
                  value={genreForm.name}
                  onChange={(e) => handleGenreChange("name", e.target.value)}
                  placeholder="Genre name"
                  required
                />
              </label>

              <label className={styles.field}>
                <span>Description</span>
                <textarea
                  className={styles.textarea}
                  value={genreForm.description}
                  onChange={(e) =>
                    handleGenreChange("description", e.target.value)
                  }
                  placeholder="Genre description"
                  rows={5}
                  required
                />
              </label>

              <label className={styles.field}>
                <span>Poster path</span>
                <input
                  className={styles.input}
                  value={genreForm.posterPath}
                  onChange={(e) =>
                    handleGenreChange("posterPath", e.target.value)
                  }
                  placeholder="/genre-poster.jpg"
                />
              </label>
            </div>

            <div className={styles.actionRow}>
              <button
                className={styles.primaryButton}
                disabled={genreLoading}
                type="submit"
              >
                {genreLoading ? "Creating..." : "Create genre"}
              </button>
            </div>

            {genreMessage && <p className={styles.success}>{genreMessage}</p>}
            {genreError && <p className={styles.error}>{genreError}</p>}
          </form>
        )}
      </div>
    </section>
  );
}
