package com.insurance.policy.repository;

import com.insurance.policy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(User.Role role);
    List<User> findByRoleOrderByCreatedAtDesc(User.Role role);
}