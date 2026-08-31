package com.insurance.policy.dto;

import com.insurance.policy.entity.Claim;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ClaimDtos {
    private ClaimDtos() {}

    public record ClaimRequest(
            @NotNull(message = "Policy id is required") @Positive(message = "Policy id must be positive") Long policyId,
            @NotNull(message = "Claim amount is required") @Positive(message = "Claim amount must be positive") BigDecimal claimAmount,
            @NotBlank(message = "Claim reason is required") @Size(max = 255, message = "Reason must be at most 255 characters") String reason,
            @NotBlank(message = "Claim description is required") @Size(max = 2000, message = "Description must be at most 2000 characters") String description) {}

    public record StatusRequest(
            @NotNull(message = "Claim status is required") Claim.ClaimStatus status,
            @Size(max = 2000, message = "Admin remarks must be at most 2000 characters") String adminRemarks) {}

    public record ClaimResponse(Long id, String claimNumber, Long policyId, String policyNumber,
                                Long customerId, String customerName, BigDecimal claimAmount, String reason,
                                String description, LocalDate claimDate, Claim.ClaimStatus status,
                                String adminRemarks) {}
}