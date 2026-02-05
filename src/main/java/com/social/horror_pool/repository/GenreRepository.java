package com.social.horror_pool.repository;

import com.social.horror_pool.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByName(String name);

    Page<Genre> findByNameLikeIgnoreCase(String s, Pageable pageable);
}
