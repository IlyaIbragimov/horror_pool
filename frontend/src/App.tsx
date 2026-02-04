import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { MoviesPage } from "./pages/MoviesPage/MoviesPage";
import { MoviePage } from "./pages/MoviePage/MoviePage";
import { GenresPage } from "./pages/GenresPage/GenresPage";
import SignUpPage from "./pages/SignUpPage/SignUpPage.tsx";
import SignInPage from "./pages/SignInPage/SignInPage";
import { AppLayout } from "./components/AppLayout.tsx";
import "./App.css";

export function App() {
  const location = useLocation();
  const state = location.state as { backgroundLocation?: Location } | null;
  const backgroundLocation = state?.backgroundLocation;

  return (
    <>
      <Routes location={backgroundLocation || location}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Navigate to="/movies" replace />} />
          <Route path="/movies" element={<MoviesPage />} />
          <Route path="/movies/:movieId" element={<MoviePage />} />
          <Route path="/genres" element={<GenresPage />} />
        </Route>

        <Route path="/login" element={<SignInPage />} />
        <Route path="/register" element={<SignUpPage />} />
      </Routes>

      {backgroundLocation && (
        <Routes>
          <Route path="/login" element={<SignInPage />} />
          <Route path="/register" element={<SignUpPage />} />
        </Routes>
      )}
    </>
  );
}
