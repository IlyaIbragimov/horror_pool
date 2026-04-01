import { useState } from "react";
import { Link } from "react-router-dom";
import { deleteGenre, editGenre } from "../../api/admin.api";
import { useAuth } from "../../auth/AuthContext";
import type { Genre } from "../../types/genre.types";
import styles from "./GenreCard.module.css";

type Props = { genre: Genre };

type GenreFormState = {
  name: string;
  description: string;
  posterPath: string;
};

function buildInitialGenreForm(genre: Genre): GenreFormState {
  return {
    name: genre.name ?? "",
    description: genre.description ?? "",
    posterPath: genre.posterPath ?? "",
  };
}

export function GenreCard({ genre }: Props) {
  const { isAdmin } = useAuth();
  const [genreData, setGenreData] = useState<Genre>(genre);
  const [showEditForm, setShowEditForm] = useState(false);
  const [formState, setFormState] = useState<GenreFormState>(
    buildInitialGenreForm(genre),
  );
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [deleted, setDeleted] = useState(false);
  const posterUrl = genreData.posterPath?.trim() || null;

  const openEditForm = () => {
    setFormState(buildInitialGenreForm(genreData));
    setShowEditForm(true);
    setError(null);
  };

  const closeEditForm = () => {
    setFormState(buildInitialGenreForm(genreData));
    setShowEditForm(false);
    setError(null);
  };

  const handleChange = (field: keyof GenreFormState, value: string) => {
    setFormState((current) => ({ ...current, [field]: value }));
  };

  const handleDeleteGenre = async () => {
    if (!isAdmin || actionLoading) return;
    if (!window.confirm("Delete this genre?")) return;

    setActionLoading(true);
    setError(null);
    try {
      await deleteGenre(genreData.genreId);
      setDeleted(true);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Delete genre failed");
    } finally {
      setActionLoading(false);
    }
  };

  const handleSubmitEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAdmin || actionLoading) return;

    const payload: Genre = {
      genreId: genreData.genreId,
      name: formState.name.trim(),
      description: formState.description.trim() || null,
      posterPath: formState.posterPath.trim() || null,
    };

    if (!payload.name) {
      setError("Genre name is required.");
      return;
    }

    setActionLoading(true);
    setError(null);
    try {
      const updatedGenre = await editGenre(genreData.genreId, payload);
      setGenreData(updatedGenre);
      setFormState(buildInitialGenreForm(updatedGenre));
      setShowEditForm(false);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Edit genre failed");
    } finally {
      setActionLoading(false);
    }
  };

  if (deleted) return null;

  return (
    <div className={styles.card}>
      <Link to={`/genre/${genreData.genreId}`} className={styles.mainLink}>
        <div className={styles.posterWrap}>
          {posterUrl ? (
            <img
              className={styles.poster}
              src={posterUrl}
              alt={genreData.name}
            />
          ) : (
            <div className={styles.noPoster}>No image</div>
          )}
        </div>

        <div className={styles.content}>
          <h3 className={styles.title}>{genreData.name}</h3>
          <p className={styles.description}>
            {genreData.description ?? "No description"}
          </p>
        </div>
      </Link>

      {isAdmin && (
        <div className={styles.adminPanel}>
          <div className={styles.actions}>
            <button
              type="button"
              className={styles.editButton}
              onClick={openEditForm}
              disabled={actionLoading}
            >
              Edit genre
            </button>
            <button
              type="button"
              className={styles.deleteButton}
              onClick={handleDeleteGenre}
              disabled={actionLoading}
            >
              Delete genre
            </button>
          </div>

          {error && <div className={styles.error}>{error}</div>}

          {showEditForm && (
            <form className={styles.form} onSubmit={handleSubmitEdit}>
              <label className={styles.field}>
                <span>Name</span>
                <input
                  className={styles.input}
                  value={formState.name}
                  onChange={(e) => handleChange("name", e.target.value)}
                  required
                />
              </label>

              <label className={styles.field}>
                <span>Poster path</span>
                <input
                  className={styles.input}
                  value={formState.posterPath}
                  onChange={(e) => handleChange("posterPath", e.target.value)}
                />
              </label>

              <label className={styles.field}>
                <span>Description</span>
                <textarea
                  className={styles.textarea}
                  value={formState.description}
                  onChange={(e) => handleChange("description", e.target.value)}
                  rows={4}
                />
              </label>

              <div className={styles.formActions}>
                <button
                  type="submit"
                  className={styles.saveButton}
                  disabled={actionLoading}
                >
                  {actionLoading ? "Saving..." : "Save changes"}
                </button>
                <button
                  type="button"
                  className={styles.cancelButton}
                  onClick={closeEditForm}
                  disabled={actionLoading}
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
      )}
    </div>
  );
}
