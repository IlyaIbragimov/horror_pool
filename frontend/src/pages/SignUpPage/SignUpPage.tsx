import { useState } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { signUp } from "../../api/auth.api";
import { useAuth } from "../../auth/AuthContext";
import { ModalShell } from "../../components/ModalShell/ModalShell";
import type { ModalRouteState } from "../../types/route.types";
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
      await signUp({ username, email, password, confirmPassword });
      await refresh();
      close();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Sign up failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ModalShell
      title="Fast registration"
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

        <div className={styles.alreadyregisted}>
          <span>Already have an account ?</span>
          <Link to="/login" replace state={{ backgroundLocation: bg }}>
            Sign In
          </Link>
        </div>

        {error && <p className={styles.error}>{error}</p>}
      </form>
    </ModalShell>
  );
}
