package com.insurance.policy.service;

import com.insurance.policy.dto.PremiumDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.service.impl.PremiumServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumServiceImplTest {
    @Mock InsuranceProductRepository productRepository;
    @InjectMocks PremiumServiceImpl premiumService;

    @Test
    void calculatesBaseAgeCoverageAndDurationAdjustments() {
        InsuranceProduct product = InsuranceProduct.builder().id(1L).basePremium(new BigDecimal("10000"))
                .coverageAmount(new BigDecimal("1000000")).status(InsuranceProduct.ProductStatus.ACTIVE).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        PremiumDtos.PremiumResponse result = premiumService.calculate(
                new PremiumDtos.PremiumRequest(1L, 55, new BigDecimal("500000"), 2));

        assertThat(result.basePremium()).isEqualByComparingTo("10000.00");
        assertThat(result.adjustment()).isEqualByComparingTo("2450.00");
        assertThat(result.finalPremium()).isEqualByComparingTo("12450.00");
    }
}