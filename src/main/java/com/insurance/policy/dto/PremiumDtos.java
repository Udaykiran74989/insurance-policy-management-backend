package com.insurance.policy.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public final class PremiumDtos {
    private PremiumDtos() {}

    public record PremiumRequest(
            @NotNull(message = "Product id is required") @Positive(message = "Product id must be positive") Long productId,
            @NotNull(message = "Age is required") @Min(value = 18, message = "Customer age must be at least 18") @Max(value = 100, message = "Customer age must be at most 100") Integer age,
            @NotNull(message = "Coverage amount is required") @Positive(message = "Coverage amount must be positive") BigDecimal coverageAmount,
            @NotNull(message = "Duration is required") @Positive(message = "Duration must be positive") Integer duration) {}

    public record PremiumResponse(BigDecimal basePremium, BigDecimal adjustment, BigDecimal finalPremium) {}
}