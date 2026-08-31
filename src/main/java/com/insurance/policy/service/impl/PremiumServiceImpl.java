package com.insurance.policy.service.impl;

import com.insurance.policy.dto.PremiumDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.exception.BadRequestException;
import com.insurance.policy.exception.ResourceNotFoundException;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private final InsuranceProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public PremiumDtos.PremiumResponse calculate(PremiumDtos.PremiumRequest request) {
        InsuranceProduct product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance product not found: " + request.productId()));
        if (product.getStatus() != InsuranceProduct.ProductStatus.ACTIVE) {
            throw new BadRequestException("Only active insurance products can be calculated");
        }
        if (request.coverageAmount().compareTo(product.getCoverageAmount()) > 0) {
            throw new BadRequestException("Coverage amount cannot exceed product coverage limit");
        }

        BigDecimal ageAdjustment = request.age() > 50
                ? product.getBasePremium().multiply(BigDecimal.valueOf(0.20))
                : request.age() < 25 ? product.getBasePremium().multiply(BigDecimal.valueOf(0.10)) : BigDecimal.ZERO;
        BigDecimal coverageAdjustment = request.coverageAmount()
                .divide(product.getCoverageAmount(), 4, RoundingMode.HALF_UP)
                .multiply(product.getBasePremium())
                .multiply(BigDecimal.valueOf(0.05));
        BigDecimal durationAdjustment = product.getBasePremium()
                .multiply(BigDecimal.valueOf(Math.max(0, request.duration() - 1)))
                .multiply(BigDecimal.valueOf(0.02));
        BigDecimal adjustment = ageAdjustment.add(coverageAdjustment).add(durationAdjustment).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalPremium = product.getBasePremium().add(adjustment).setScale(2, RoundingMode.HALF_UP);
        return new PremiumDtos.PremiumResponse(product.getBasePremium().setScale(2, RoundingMode.HALF_UP), adjustment, finalPremium);
    }
}