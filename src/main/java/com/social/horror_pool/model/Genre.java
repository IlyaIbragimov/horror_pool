package com.social.horror_pool.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Genre {
    @Id
    @EqualsAndHashCode.Include
    private Long genreId;

    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies;
}
