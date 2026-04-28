import { useState } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { signIn } from "../../api/auth.api";
import { useAuth } from "../../auth/AuthContext";
import { ModalShell } from "../../components/ModalShell/ModalShell";
import type { ModalRouteState } from "../../types/route.types";
import styles from "./SignInPage.module.css";

export default function SignInPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { refresh } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const bg = (location.state as ModalRouteState | null)?.backgroundLocation;

  const close = () => {
    if (bg) navigate(bg.pathname + bg.search, { replace: true });
    else navigate("/movies", { replace: true });
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await signIn(username, password);
      await refresh();
      close();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Sign in failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ModalShell
      title="Sign in with your username and password"
      onClose={close}
      overlayClassName={styles.overlay}
      modalClassName={styles.modal}
      headerClassName={styles.header}
      titleClassName={styles.title}
      closeButtonClassName={styles.closeBtn}
    >
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

        <div className={styles.register_link}>
          <span>Don't have an account ?</span>
          <Link to="/register" replace state={{ backgroundLocation: bg }}>
            Register
          </Link>
        </div>

        {error && <p className={styles.error}>{error}</p>}
      </form>
    </ModalShell>
  );
}
