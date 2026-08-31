package com.insurance.policy.service;

import com.insurance.policy.dto.ClaimDtos;
import com.insurance.policy.entity.Claim;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.repository.ClaimRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {
    @Mock ClaimRepository claimRepository;
    @Mock PolicyRepository policyRepository;
    @Mock UserRepository userRepository;
    @InjectMocks ClaimServiceImpl claimService;

    @Test
    void customerCanSubmitClaimForOwnActivePolicy() {
        User customer = User.builder().id(5L).name("Customer").email("customer@example.com").build();
        Policy policy = Policy.builder().id(10L).policyNumber("POL-2026-000010").customer(customer)
                .coverageAmount(new BigDecimal("500000")).status(Policy.PolicyStatus.ACTIVE).build();
        when(userRepository.findByEmailIgnoreCase("customer@example.com")).thenReturn(Optional.of(customer));
        when(policyRepository.findByIdAndCustomerId(10L, 5L)).thenReturn(Optional.of(policy));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> {
            Claim claim = invocation.getArgument(0);
            if (claim.getId() == null) claim.setId(22L);
            return claim;
        });

        ClaimDtos.ClaimResponse result = claimService.submit("customer@example.com",
                new ClaimDtos.ClaimRequest(10L, new BigDecimal("100000"), "Accident", "Vehicle accident"));

        assertThat(result.claimNumber()).startsWith("CLM-");
        assertThat(result.status()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
    }

    @Test
    void adminCanApproveClaimOnlyAfterReview() {
        User customer = User.builder().id(5L).name("Customer").build();
        Policy policy = Policy.builder().id(10L).policyNumber("POL-10").customer(customer).build();
        Claim claim = Claim.builder().id(22L).claimNumber("CLM-22").policy(policy)
                .status(Claim.ClaimStatus.UNDER_REVIEW).claimAmount(new BigDecimal("100"))
                .reason("Accident").description("Details").build();
        when(claimRepository.findById(22L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(claim)).thenReturn(claim);

        ClaimDtos.ClaimResponse result = claimService.updateStatus(22L,
                new ClaimDtos.StatusRequest(Claim.ClaimStatus.APPROVED, "Verified and approved"));

        assertThat(result.status()).isEqualTo(Claim.ClaimStatus.APPROVED);
        assertThat(result.adminRemarks()).isEqualTo("Verified and approved");
    }
}