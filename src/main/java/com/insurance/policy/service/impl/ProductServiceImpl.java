package com.insurance.policy.service.impl;

import com.insurance.policy.dto.ProductDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.exception.ResourceNotFoundException;
import com.insurance.policy.repository.InsuranceProductRepository;
import com.insurance.policy.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final InsuranceProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDtos.ProductResponse> findAll(InsuranceProduct.ProductType type) {
        List<InsuranceProduct> products = type == null
                ? productRepository.findByStatus(InsuranceProduct.ProductStatus.ACTIVE)
                : productRepository.findByProductTypeAndStatus(type, InsuranceProduct.ProductStatus.ACTIVE);
        return products.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDtos.ProductResponse findById(Long id) {
        return toResponse(findProduct(id));
    }

    @Override
    @Transactional
    public ProductDtos.ProductResponse create(ProductDtos.ProductRequest request) {
        InsuranceProduct product = InsuranceProduct.builder()
                .productName(request.productName().trim())
                .productType(request.productType())
                .description(request.description().trim())
                .coverageAmount(request.coverageAmount())
                .basePremium(request.basePremium())
                .status(InsuranceProduct.ProductStatus.ACTIVE)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDtos.ProductResponse update(Long id, ProductDtos.ProductRequest request) {
        InsuranceProduct product = findProduct(id);
        product.setProductName(request.productName().trim());
        product.setProductType(request.productType());
        product.setDescription(request.description().trim());
        product.setCoverageAmount(request.coverageAmount());
        product.setBasePremium(request.basePremium());
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        InsuranceProduct product = findProduct(id);
        product.setStatus(InsuranceProduct.ProductStatus.INACTIVE);
        productRepository.save(product);
        log.info("Deactivated insurance product {}", id);
    }

    private InsuranceProduct findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance product not found: " + id));
    }

    private ProductDtos.ProductResponse toResponse(InsuranceProduct product) {
        return new ProductDtos.ProductResponse(product.getId(), product.getProductName(), product.getProductType(),
                product.getDescription(), product.getCoverageAmount(), product.getBasePremium(), product.getStatus(),
                product.getCreatedAt());
    }
}