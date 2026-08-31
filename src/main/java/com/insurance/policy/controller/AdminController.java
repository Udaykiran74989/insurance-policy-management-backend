package com.insurance.policy.controller;

import com.insurance.policy.dto.AdminDtos;
import com.insurance.policy.dto.ClaimDtos;
import com.insurance.policy.dto.PolicyDtos;
import com.insurance.policy.service.AdminService;
import com.insurance.policy.service.ClaimService;
import com.insurance.policy.service.CustomerService;
import com.insurance.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration")
public class AdminController {
    private final AdminService adminService;
    private final CustomerService customerService;
    private final PolicyService policyService;
    private final ClaimService claimService;

    @GetMapping("/dashboard")
    @Operation(summary = "View the admin dashboard summary")
    public AdminDtos.DashboardResponse dashboard() {
        return adminService.dashboard();
    }

    @GetMapping("/customers")
    @Operation(summary = "List all customer accounts")
    public List<com.insurance.policy.dto.UserDtos.UserResponse> customers() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/policies")
    @Operation(summary = "List all policies")
    public List<PolicyDtos.PolicyResponse> policies() {
        return policyService.findAll();
    }

    @PutMapping("/policies/{id}/status")
    @Operation(summary = "Update a policy status")
    public PolicyDtos.PolicyResponse updatePolicyStatus(@PathVariable Long id,
                                                        @Valid @RequestBody PolicyDtos.StatusRequest request) {
        return policyService.updateStatus(id, request);
    }

    @GetMapping("/claims")
    @Operation(summary = "List all claims")
    public List<ClaimDtos.ClaimResponse> claims() {
        return claimService.findAll();
    }

    @PutMapping("/claims/{id}/status")
    @Operation(summary = "Move a claim through review, approval or rejection")
    public ClaimDtos.ClaimResponse updateClaimStatus(@PathVariable Long id,
                                                     @Valid @RequestBody ClaimDtos.StatusRequest request) {
        return claimService.updateStatus(id, request);
    }
}