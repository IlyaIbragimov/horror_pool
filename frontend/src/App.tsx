import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import { MoviesPage } from "./pages/MoviesPage/MoviesPage";
import { MoviePage } from "./pages/MoviePage/MoviePage";
import { GenresPage } from "./pages/GenresPage/GenresPage";
import { PublicWatchlistPage } from "./pages/PublicWatchlistsPage/PublicWatchlistPage.tsx";
import { UserWatchlistPage } from "./pages/UserWatchlistPage/UserWatchlistPage.tsx";
import { WatchlistPage } from "./pages/WatchlistPage/WatchlistPage.tsx";
import SignUpPage from "./pages/SignUpPage/SignUpPage.tsx";
import SignInPage from "./pages/SignInPage/SignInPage";
import { AddWatchlistPage } from "./pages/AddWatchlistPage/AddWatchlistPage.tsx";
import { AppLayout } from "./components/AppLayout.tsx";
import { AdminPage } from "./pages/AdminPage/AdminPage.tsx";
import { GenrePage } from "./pages/GenrePage/GenrePage.tsx";
import { useAuth } from "./auth/AuthContext";
import "./App.css";

export function App() {
  const location = useLocation();
  const state = location.state as { backgroundLocation?: Location } | null;
  const backgroundLocation = state?.backgroundLocation;

  function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { user, loading } = useAuth();
    if (loading) return null;
    if (!user) return <Navigate to="/movies" replace />;
    return <>{children}</>;
  }

  function AdminRoute({ children }: { children: React.ReactNode }) {
    const { user, isAdmin, loading } = useAuth();
    if (loading) return null;
    if (!user || !isAdmin) return <Navigate to="/movies" replace />;
    return <>{children}</>;
  }

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
          <Route path="/genre/:genreId" element={<GenrePage />} />
          <Route
            path="/watchlistUser"
            element={
              <ProtectedRoute>
                <UserWatchlistPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/adminPanel"
            element={
              <AdminRoute>
                <AdminPage />
              </AdminRoute>
            }
          />
        </Route>

        <Route path="/login" element={<SignInPage />} />
        <Route path="/register" element={<SignUpPage />} />
        <Route
          path="/addMovieToWatchlist"
          element={
            <ProtectedRoute>
              <AddWatchlistPage />
            </ProtectedRoute>
          }
        />
      </Routes>

      {backgroundLocation && (
        <Routes>
          <Route path="/login" element={<SignInPage />} />
          <Route path="/register" element={<SignUpPage />} />
          <Route path="/addMovieToWatchlist" element={<AddWatchlistPage />} />
        </Routes>
      )}
    </>
  );
}
