package com.insurance.policy.service.impl;

import com.insurance.policy.dto.ClaimDtos;
import com.insurance.policy.entity.Claim;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.exception.BadRequestException;
import com.insurance.policy.exception.ResourceNotFoundException;
import com.insurance.policy.repository.ClaimRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimServiceImpl implements ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClaimDtos.ClaimResponse submit(String email, ClaimDtos.ClaimRequest request) {
        User customer = findUser(email);
        Policy policy = policyRepository.findByIdAndCustomerId(request.policyId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active customer policy not found: " + request.policyId()));
        if (policy.getStatus() != Policy.PolicyStatus.ACTIVE) {
            throw new BadRequestException("Claims can only be submitted for active policies");
        }
        if (request.claimAmount().compareTo(policy.getCoverageAmount()) > 0) {
            throw new BadRequestException("Claim amount cannot exceed policy coverage");
        }
        Claim claim = Claim.builder()
                .claimNumber("PENDING")
                .policy(policy)
                .claimAmount(request.claimAmount())
                .reason(request.reason().trim())
                .description(request.description().trim())
                .claimDate(LocalDate.now())
                .status(Claim.ClaimStatus.SUBMITTED)
                .build();
        Claim saved = claimRepository.save(claim);
        saved.setClaimNumber(String.format("CLM-%d-%06d", Year.now().getValue(), saved.getId()));
        saved = claimRepository.save(saved);
        log.info("Submitted claim {} for policy {}", saved.getClaimNumber(), policy.getPolicyNumber());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDtos.ClaimResponse> findMine(String email) {
        User customer = findUser(email);
        return claimRepository.findByPolicyCustomerIdOrderByCreatedAtDesc(customer.getId()).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDtos.ClaimResponse findMineById(String email, Long id) {
        User customer = findUser(email);
        return toResponse(claimRepository.findByIdAndPolicyCustomerId(id, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDtos.ClaimResponse> findAll() {
        return claimRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ClaimDtos.ClaimResponse updateStatus(Long id, ClaimDtos.StatusRequest request) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + id));
        Claim.ClaimStatus current = claim.getStatus();
        Claim.ClaimStatus next = request.status();
        boolean valid = (current == Claim.ClaimStatus.SUBMITTED && next == Claim.ClaimStatus.UNDER_REVIEW)
                || (current == Claim.ClaimStatus.UNDER_REVIEW
                && (next == Claim.ClaimStatus.APPROVED || next == Claim.ClaimStatus.REJECTED));
        if (!valid) {
            throw new BadRequestException("Invalid claim status transition from " + current + " to " + next);
        }
        if (next == Claim.ClaimStatus.REJECTED && (request.adminRemarks() == null || request.adminRemarks().isBlank())) {
            throw new BadRequestException("Admin remarks are required when rejecting a claim");
        }
        claim.setStatus(next);
        claim.setAdminRemarks(request.adminRemarks());
        log.info("Updated claim {} from {} to {}", claim.getClaimNumber(), current, next);
        return toResponse(claimRepository.save(claim));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found"));
    }

    private ClaimDtos.ClaimResponse toResponse(Claim claim) {
        Policy policy = claim.getPolicy();
        User customer = policy.getCustomer();
        return new ClaimDtos.ClaimResponse(claim.getId(), claim.getClaimNumber(), policy.getId(),
                policy.getPolicyNumber(), customer.getId(), customer.getName(), claim.getClaimAmount(),
                claim.getReason(), claim.getDescription(), claim.getClaimDate(), claim.getStatus(),
                claim.getAdminRemarks());
    }
}