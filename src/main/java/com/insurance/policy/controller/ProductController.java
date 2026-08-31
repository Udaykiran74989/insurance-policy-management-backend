package com.insurance.policy.controller;

import com.insurance.policy.dto.ProductDtos;
import com.insurance.policy.entity.InsuranceProduct;
import com.insurance.policy.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Insurance Products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List active products, optionally filtered by type")
    public List<ProductDtos.ProductResponse> list(@RequestParam(required = false) InsuranceProduct.ProductType type) {
        return productService.findAll(type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "View an insurance product")
    public ProductDtos.ProductResponse get(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an insurance product")
    public ProductDtos.ProductResponse create(@Valid @RequestBody ProductDtos.ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an insurance product")
    public ProductDtos.ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductDtos.ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate an insurance product")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}