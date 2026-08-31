package com.insurance.policy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basePremium;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ProductType {
        HEALTH, LIFE, VEHICLE, TRAVEL
    }

    public enum ProductStatus {
        ACTIVE, INACTIVE
    }
}