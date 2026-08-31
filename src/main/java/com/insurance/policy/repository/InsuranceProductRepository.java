package com.insurance.policy.repository;

import com.insurance.policy.entity.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
    List<InsuranceProduct> findByStatus(InsuranceProduct.ProductStatus status);
    List<InsuranceProduct> findByProductTypeAndStatus(InsuranceProduct.ProductType type,
                                                       InsuranceProduct.ProductStatus status);
}