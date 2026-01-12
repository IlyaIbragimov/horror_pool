import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signUp } from "../../api/auth.api";
import { useAuth } from "../../auth/AuthContext";
import styles from "./SignUpPage.module.css";


export default function SignUpPage() {
  
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { refresh } = useAuth();
  const navigate = useNavigate();
  const close = () => navigate(-1);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await signUp({username, email, password, confirmPassword});
      await refresh();
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
          <h1 className={styles.title}>Fast registration</h1>
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
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="email"
            autoComplete="email"
          />

          <input
            className={styles.input}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            type="password"
            autoComplete="current-password"
          />

          <input
            className={styles.input}
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm Password"
            type="password"
            autoComplete="confirm password"
          />

          <button className={styles.submit} disabled={loading} type="submit">
            {loading ? "Registering..." : "Sign Up"}
          </button>

          {error && <p className={styles.error}>{error}</p>}
        </form>
      </div>
    </div>
  );
}