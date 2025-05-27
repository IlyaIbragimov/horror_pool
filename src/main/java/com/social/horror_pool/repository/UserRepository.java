package com.social.horror_pool.repository;

import com.social.horror_pool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
