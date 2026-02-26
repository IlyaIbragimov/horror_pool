import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { MoviesPage } from "./pages/MoviesPage/MoviesPage";
import { MoviePage } from "./pages/MoviePage/MoviePage";
import { GenresPage } from "./pages/GenresPage/GenresPage";
import { PublicWatchlistPage } from "./pages/PublicWatchlistsPage/PublicWatchlistPage.tsx";
import { UserWatchlistPage } from "./pages/UserPage/UserWatchlistPage.tsx";
import { WatchlistPage } from "./pages/WatchlistPage/WatchlistPage.tsx";
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
          <Route path="/watchlistPublic" element={<PublicWatchlistPage />} />
          <Route path="/watchlist/:watchlistId" element={<WatchlistPage />} />
          <Route path="/watchlistUser" element={<UserWatchlistPage />} />
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
