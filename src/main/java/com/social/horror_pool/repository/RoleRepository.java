package com.social.horror_pool.repository;

import com.social.horror_pool.configuration.RoleName;
import com.social.horror_pool.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}
