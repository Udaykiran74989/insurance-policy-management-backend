package com.insurance.policy.dto;

import com.insurance.policy.entity.InsuranceProduct;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProductDtos {
    private ProductDtos() {}

    public record ProductRequest(
            @NotBlank(message = "Product name is required") @Size(max = 150, message = "Product name must be at most 150 characters")
            String productName,
            @NotNull(message = "Product type is required") InsuranceProduct.ProductType productType,
            @NotBlank(message = "Description is required") @Size(max = 1000, message = "Description must be at most 1000 characters")
            String description,
            @NotNull(message = "Coverage amount is required") @Positive(message = "Coverage amount must be positive")
            BigDecimal coverageAmount,
            @NotNull(message = "Base premium is required") @Positive(message = "Base premium must be positive")
            BigDecimal basePremium) {}

    public record ProductResponse(Long id, String productName, InsuranceProduct.ProductType productType,
                                  String description, BigDecimal coverageAmount, BigDecimal basePremium,
                                  InsuranceProduct.ProductStatus status, LocalDateTime createdAt) {}
}