package com.insurance.policy.service;

import com.insurance.policy.dto.ProductDtos;
import com.insurance.policy.entity.InsuranceProduct;

import java.util.List;

public interface ProductService {
    List<ProductDtos.ProductResponse> findAll(InsuranceProduct.ProductType type);
    ProductDtos.ProductResponse findById(Long id);
    ProductDtos.ProductResponse create(ProductDtos.ProductRequest request);
    ProductDtos.ProductResponse update(Long id, ProductDtos.ProductRequest request);
    void delete(Long id);
}