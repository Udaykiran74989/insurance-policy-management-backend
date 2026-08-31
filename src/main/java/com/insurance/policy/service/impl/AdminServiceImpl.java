package com.insurance.policy.service.impl;

import com.insurance.policy.dto.AdminDtos;
import com.insurance.policy.entity.Claim;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.repository.ClaimRepository;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final InsuranceProductRepository productRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDtos.DashboardResponse dashboard() {
        long pending = claimRepository.countByStatus(Claim.ClaimStatus.SUBMITTED)
                + claimRepository.countByStatus(Claim.ClaimStatus.UNDER_REVIEW);
        return new AdminDtos.DashboardResponse(
                userRepository.countByRole(User.Role.CUSTOMER),
                productRepository.count(),
                policyRepository.countByStatus(Policy.PolicyStatus.ACTIVE),
                pending,
                claimRepository.countByStatus(Claim.ClaimStatus.APPROVED),
                claimRepository.countByStatus(Claim.ClaimStatus.REJECTED));
    }
}