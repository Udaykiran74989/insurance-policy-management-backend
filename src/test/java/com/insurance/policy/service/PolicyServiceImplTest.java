package com.insurance.policy.service;

import com.insurance.policy.dto.PolicyDtos;
import com.insurance.policy.dto.PremiumDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.impl.PolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {
    @Mock PolicyRepository policyRepository;
    @Mock UserRepository userRepository;
    @Mock InsuranceProductRepository productRepository;
    @Mock PremiumService premiumService;
    @InjectMocks PolicyServiceImpl policyService;

    @Test
    void purchaseCalculatesPremiumAndCreatesActivePolicy() {
        User user = User.builder().id(7L).email("customer@example.com").name("Customer").build();
        InsuranceProduct product = InsuranceProduct.builder().id(3L).productName("Health")
                .coverageAmount(new BigDecimal("1000000")).status(InsuranceProduct.ProductStatus.ACTIVE).build();
        when(userRepository.findByEmailIgnoreCase("customer@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(premiumService.calculate(any())).thenReturn(
                new PremiumDtos.PremiumResponse(new BigDecimal("10000"), new BigDecimal("500"), new BigDecimal("10500")));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> {
            Policy policy = invocation.getArgument(0);
            if (policy.getId() == null) policy.setId(42L);
            return policy;
        });

        PolicyDtos.PolicyResponse result = policyService.purchase("customer@example.com",
                new PolicyDtos.PurchaseRequest(3L, 35, new BigDecimal("500000"), 1));

        assertThat(result.policyNumber()).startsWith("POL-");
        assertThat(result.status()).isEqualTo(Policy.PolicyStatus.ACTIVE);
        assertThat(result.premiumAmount()).isEqualByComparingTo("10500");
        verify(premiumService).calculate(any());
        verify(policyRepository, times(2)).save(any(Policy.class));
    }
}