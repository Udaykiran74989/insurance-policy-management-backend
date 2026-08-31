package com.insurance.policy.dto;

import com.insurance.policy.entity.Policy;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class PolicyDtos {
    private PolicyDtos() {}

    public record PurchaseRequest(
            @NotNull(message = "Product id is required") @Positive(message = "Product id must be positive") Long productId,
            @NotNull(message = "Age is required") @Min(value = 18, message = "Customer age must be at least 18") @Max(value = 100, message = "Customer age must be at most 100") Integer age,
            @NotNull(message = "Coverage amount is required") @Positive(message = "Coverage amount must be positive") BigDecimal coverageAmount,
            @NotNull(message = "Duration is required") @Positive(message = "Duration must be positive") Integer duration) {}

    public record StatusRequest(@NotNull(message = "Policy status is required") Policy.PolicyStatus status) {}

    public record PolicyResponse(Long id, String policyNumber, Long customerId, String customerName,
                                 Long productId, String productName, LocalDate startDate, LocalDate endDate,
                                 BigDecimal coverageAmount, BigDecimal premiumAmount, Policy.PolicyStatus status) {}
}