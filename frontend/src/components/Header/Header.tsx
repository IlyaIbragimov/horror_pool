import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";
import "./Header.css";
import tmdblogo from "../../assets/logos/tmdblogo.svg"
import logo from "../../assets/logos/logo.svg"
import { useNavigate } from "react-router-dom"

export default function Header() {

  const [mobileOpen, setMobileOpen] = useState(false);
  const [query, setQuery] = useState("");
  const toggleMobileMenu = () => setMobileOpen((prev) => !prev);
  const { user, loading, logout } = useAuth();
  const navigate = useNavigate();
  const onSubmitSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const q = query.trim();
    const params = new URLSearchParams();
    if (q) params.set("keyword", q);
    params.set("page", "1");
    navigate(`/movies?${params.toString()}`);
    setQuery("");
  };
  
  return (
    <header className="header">
        <div className="header-wrapper">

          <div className="header-logo">
            <div className="logo">
             <a> <img src={logo} alt="" /> </a>
            </div>
            <div className="logo">
              <a href="https://www.themoviedb.org" target="_blank" rel="noreferrer" aria-label="TMDB">
                <img src={tmdblogo} alt="TMDB" />
              </a>
            </div>
          </div>

          <nav className="header-menu desktop-menu" aria-label="Main navigation">
            <Link className="header-menu-item" to="/movies">
              Movies
            </Link>
            <Link className="header-menu-item" to="/genres">
              Genres
            </Link>
            <Link className="header-menu-item" to="/genres">
              Templates
            </Link>
          </nav>

          <div className="search-wrapper">
            <form className="search-form" onSubmit={onSubmitSearch} role="search">
              <input className="search-input" value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search movies..." aria-label="Search movies"/>
              <button className="search-btn" type="submit">Search</button>
            </form>
          </div>

          <div className="header-actions">
            {loading ? null : user ? (
              <div className="buttons">
                <span style={{ opacity: 0.9, marginRight: 10 }}>{user}</span>
                <button className="btn btn-ghost" onClick={logout} type="button">
                  Sign out
                </button>
              </div>
            ) : (
              <div className="buttons">
                <Link className="btn btn-ghost" to="/login">
                  Sign in
                </Link>
                <Link className="btn btn-primary" to="/register">
                  Sign up
                </Link>
              </div>
            )}
          </div>

          <button className="burger" onClick={toggleMobileMenu} aria-label="Toggle mobile menu" aria-expanded={mobileOpen} type="button">
            {mobileOpen ? "✕" : "☰"}
          </button>
      </div>

        <div className={`mobile-menu ${mobileOpen ? "open" : ""}`}>
            <Link to="/movies" onClick={() => setMobileOpen(false)}>
              Movies
            </Link>
            <Link to="/genres" onClick={() => setMobileOpen(false)}>
              Genres
            </Link>
            <a href="#templates" onClick={() => setMobileOpen(false)}>
              New popular
            </a>

            <div className="mobile-actions">
              {loading ? null : user ? (
                <>
                  <button type="button" className="btn btn-primary" onClick={async () => { await logout(); setMobileOpen(false);}}>
                      Sign out
                  </button>
                </>
              ) : ( 
                <>
                  <Link className="btn btn-ghost" to="/login" onClick={() => setMobileOpen(false)}>
                    Sign in
                  </Link>
                  <Link className="btn btn-primary" to="/register" onClick={() => setMobileOpen(false)}>
                    Sign up
                  </Link>
                </>
              )}
          </div>
        </div>
    </header>
  );
}