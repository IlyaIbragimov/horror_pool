import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signIn } from "../../api/auth.api";
import styles from "./SignInPage.module.css";

export default function SignInPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  const close = () => navigate(-1);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await signIn(username, password);
      close();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Sign in failed");
    } finally {
      setLoading(false);
    }
  };

  const onOverlayMouseDown = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) close();
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === "Escape") close();
  };

  return (
    <div
      className={styles.overlay}
      onMouseDown={onOverlayMouseDown}
      onKeyDown={onKeyDown}
      tabIndex={-1}
      aria-modal="true"
      role="dialog"
    >
      <div className={styles.modal}>
        <div className={styles.header}>
          <h1 className={styles.title}>Sign in with your username and password</h1>
          <button className={styles.closeBtn} onClick={close} type="button" aria-label="Close">
            ✕
          </button>
        </div>

        <form className={styles.body} onSubmit={onSubmit}>
          <input
            className={styles.input}
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Username"
            autoComplete="username"
          />

          <input
            className={styles.input}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            type="password"
            autoComplete="current-password"
          />

          <button className={styles.submit} disabled={loading} type="submit">
            {loading ? "Signing in..." : "Sign in"}
          </button>

          {error && <p className={styles.error}>{error}</p>}
        </form>
      </div>
    </div>
  );
}