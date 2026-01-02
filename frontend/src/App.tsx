import { Routes, Route, Navigate } from "react-router-dom";
import { MoviesPage } from "./pages/MoviesPage/MoviesPage";
import { MoviePage } from "./pages/MoviePage/MoviePage";
import { AppLayout } from "./components/AppLayout.tsx";

export function App() {
  return (
    <AppLayout>
    <Routes>
      <Route path="/" element={<Navigate to="/movies" replace />} />
      <Route path="/movies" element={<MoviesPage />} />
      <Route path="/movies/:movieId" element={<MoviePage />} />
    </Routes>
    </AppLayout>
  );
}