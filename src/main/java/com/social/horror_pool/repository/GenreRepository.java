package com.social.horror_pool.repository;

import com.social.horror_pool.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Long, Genre> {
}
