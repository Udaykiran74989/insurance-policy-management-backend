package com.insurance.policy.config;

import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.entity.Claim;
import com.insurance.policy.entity.Policy;
import com.insurance.policy.entity.User;
import com.insurance.policy.repository.ClaimRepository;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.repository.PolicyRepository;
import com.insurance.policy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final InsuranceProductRepository productRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }
        userRepository.save(User.builder().name("System Admin")
                .email("admin@insurance.com")
                .password(passwordEncoder.encode("Admin@123")).phone("9000000000").address("Head Office")
                .role(User.Role.ADMIN).status(User.UserStatus.ACTIVE).build());
        User uday = userRepository.save(User.builder().name("Uday Gajjalwar").email("uday@gmail.com")
                .password(passwordEncoder.encode("uday@123")).phone("9000000001").address("Bengaluru")
                .role(User.Role.CUSTOMER).status(User.UserStatus.ACTIVE).build());
        User rutwik = userRepository.save(User.builder().name("Rutwik Gudpale").email("rutwik@gmail.com")
                .password(passwordEncoder.encode("rutik@123")).phone("9000000002").address("Mumbai")
                .role(User.Role.CUSTOMER).status(User.UserStatus.ACTIVE).build());

        InsuranceProduct health = saveProduct("Secure Health Plus", InsuranceProduct.ProductType.HEALTH,
                "Hospitalisation and medical expense cover for individuals.", "1000000", "12000");
        InsuranceProduct life = saveProduct("Family Life Shield", InsuranceProduct.ProductType.LIFE,
                "Term life protection for your family's long-term security.", "2500000", "18000");
        InsuranceProduct vehicle = saveProduct("DriveCare Comprehensive", InsuranceProduct.ProductType.VEHICLE,
                "Comprehensive cover for accidental damage and third-party liability.", "800000", "9500");
        saveProduct("Global Travel Assist", InsuranceProduct.ProductType.TRAVEL,
                "Travel medical, baggage and trip assistance for international journeys.", "500000", "5000");

        LocalDate today = LocalDate.now();
        Policy anitaPolicy = policyRepository.save(Policy.builder().policyNumber("POL-" + today.getYear() + "-000001")
                .customer(uday).insuranceProduct(health).startDate(today).endDate(today.plusYears(1))
                .coverageAmount(new BigDecimal("500000")).premiumAmount(new BigDecimal("12600"))
                .status(Policy.PolicyStatus.ACTIVE).build());
        Policy rahulPolicy = policyRepository.save(Policy.builder().policyNumber("POL-" + today.getYear() + "-000002")
                .customer(rutwik).insuranceProduct(vehicle).startDate(today.minusMonths(2)).endDate(today.plusMonths(10))
                .coverageAmount(new BigDecimal("400000")).premiumAmount(new BigDecimal("9690"))
                .status(Policy.PolicyStatus.ACTIVE).build());
        claimRepository.save(Claim.builder().claimNumber("CLM-" + today.getYear() + "-000001")
                .policy(anitaPolicy).claimAmount(new BigDecimal("75000")).reason("Hospitalisation")
                .description("Sample medical claim awaiting review.").claimDate(today)
                .status(Claim.ClaimStatus.SUBMITTED).build());
        claimRepository.save(Claim.builder().claimNumber("CLM-" + today.getYear() + "-000002")
                .policy(rahulPolicy).claimAmount(new BigDecimal("120000")).reason("Accident damage")
                .description("Sample vehicle repair claim approved by the admin.").claimDate(today.minusDays(5))
                .status(Claim.ClaimStatus.APPROVED).adminRemarks("Documents verified").build());
    }

    private InsuranceProduct saveProduct(String name, InsuranceProduct.ProductType type, String description,
                             String coverage, String premium) {
        return productRepository.save(InsuranceProduct.builder().productName(name).productType(type)
                .description(description).coverageAmount(new BigDecimal(coverage))
                .basePremium(new BigDecimal(premium)).status(InsuranceProduct.ProductStatus.ACTIVE).build());
    }
}