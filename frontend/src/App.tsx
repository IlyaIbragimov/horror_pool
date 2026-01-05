import { Routes, Route, Navigate } from "react-router-dom";
import { MoviesPage } from "./pages/MoviesPage/MoviesPage";
import { MoviePage } from "./pages/MoviePage/MoviePage";
import { GenresPage } from "./pages/GenresPage/GenresPage";
import { AppLayout } from "./components/AppLayout.tsx";
import './App.css'

export function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
       <Route path="/" element={<Navigate to="/movies" replace />} />
       <Route path="/movies" element={<MoviesPage />} />
       <Route path="/movies/:movieId" element={<MoviePage />} />
       <Route path="/genres" element={<GenresPage />} />
      </Route>
    </Routes>
  );
}