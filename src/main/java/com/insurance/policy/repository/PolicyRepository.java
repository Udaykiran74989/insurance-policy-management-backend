package com.insurance.policy.repository;

import com.insurance.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<Policy> findByIdAndCustomerId(Long id, Long customerId);
    long countByStatus(Policy.PolicyStatus status);
}