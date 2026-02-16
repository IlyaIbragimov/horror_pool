package com.social.horror_pool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long watchlistId;

    private String title;

    private boolean isPublic;

    private double rating;

    private int rateCount;

    @ManyToMany
    @JoinTable(
    name = "watchlist_raters",
    joinColumns = @JoinColumn(name = "watchlist_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"),
    uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "user_id"})
)
    private Set<User> raters = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "watchlist_followers",
            joinColumns = @JoinColumn(name = "watchlist_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"watchlist_id", "user_id"})
    )
    private Set<User> followers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistItem> watchlistItems = new ArrayList<>();
}
