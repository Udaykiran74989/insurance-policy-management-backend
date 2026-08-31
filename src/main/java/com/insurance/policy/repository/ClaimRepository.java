package com.insurance.policy.repository;

import com.insurance.policy.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<Claim> findByIdAndPolicyCustomerId(Long id, Long customerId);
    long countByStatus(Claim.ClaimStatus status);
}