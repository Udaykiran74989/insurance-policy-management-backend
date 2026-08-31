package com.insurance.policy.service.impl;

import com.insurance.policy.dto.PolicyDtos;
import com.insurance.policy.dto.PremiumDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.exception.BadRequestException;
import com.insurance.policy.exception.ResourceNotFoundException;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.PolicyService;
import com.insurance.policy.service.PremiumService;
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
public class PolicyServiceImpl implements PolicyService {
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final InsuranceProductRepository productRepository;
    private final PremiumService premiumService;

    @Override
    @Transactional(readOnly = true)
    public List<PolicyDtos.PolicyResponse> findMine(String email) {
        User customer = findUser(email);
        return policyRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyDtos.PolicyResponse findMineById(String email, Long id) {
        User customer = findUser(email);
        return toResponse(policyRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + id)));
    }

    @Override
    @Transactional
    public PolicyDtos.PolicyResponse purchase(String email, PolicyDtos.PurchaseRequest request) {
        User customer = findUser(email);
        InsuranceProduct product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance product not found: " + request.productId()));
        PremiumDtos.PremiumResponse premium = premiumService.calculate(
                new PremiumDtos.PremiumRequest(request.productId(), request.age(), request.coverageAmount(), request.duration()));
        LocalDate startDate = LocalDate.now();
        Policy policy = Policy.builder()
                .policyNumber("PENDING")
                .customer(customer)
                .insuranceProduct(product)
                .startDate(startDate)
                .endDate(startDate.plusYears(request.duration()))
                .coverageAmount(request.coverageAmount())
                .premiumAmount(premium.finalPremium())
                .status(Policy.PolicyStatus.ACTIVE)
                .build();
        Policy saved = policyRepository.save(policy);
        saved.setPolicyNumber(String.format("POL-%d-%06d", Year.now().getValue(), saved.getId()));
        saved = policyRepository.save(saved);
        log.info("Created policy {} for customer {}", saved.getPolicyNumber(), customer.getEmail());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PolicyDtos.PolicyResponse cancel(String email, Long id) {
        User customer = findUser(email);
        Policy policy = policyRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + id));
        if (policy.getStatus() != Policy.PolicyStatus.ACTIVE) {
            throw new BadRequestException("Only active policies can be cancelled");
        }
        policy.setStatus(Policy.PolicyStatus.CANCELLED);
        return toResponse(policyRepository.save(policy));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyDtos.PolicyResponse> findAll() {
        return policyRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PolicyDtos.PolicyResponse updateStatus(Long id, PolicyDtos.StatusRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + id));
        policy.setStatus(request.status());
        return toResponse(policyRepository.save(policy));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found"));
    }

    private PolicyDtos.PolicyResponse toResponse(Policy policy) {
        return new PolicyDtos.PolicyResponse(policy.getId(), policy.getPolicyNumber(), policy.getCustomer().getId(),
                policy.getCustomer().getName(), policy.getInsuranceProduct().getId(),
                policy.getInsuranceProduct().getProductName(), policy.getStartDate(), policy.getEndDate(),
                policy.getCoverageAmount(), policy.getPremiumAmount(), policy.getStatus());
    }
}