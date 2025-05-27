package com.social.horror_pool.model;

import com.social.horror_pool.configuration.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @EqualsAndHashCode.Include
    private RoleName roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
