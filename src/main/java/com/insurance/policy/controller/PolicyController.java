package com.insurance.policy.controller;

import com.insurance.policy.dto.PolicyDtos;
import com.insurance.policy.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Customer Policies")
@PreAuthorize("hasRole('CUSTOMER')")
public class PolicyController {
    private final PolicyService policyService;

    @GetMapping
    @Operation(summary = "List policies belonging to the authenticated customer")
    public List<PolicyDtos.PolicyResponse> list(Authentication authentication) {
        return policyService.findMine(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "View one of the authenticated customer's policies")
    public PolicyDtos.PolicyResponse get(@PathVariable Long id, Authentication authentication) {
        return policyService.findMineById(authentication.getName(), id);
    }

    @PostMapping
    @Operation(summary = "Purchase a policy")
    public PolicyDtos.PolicyResponse purchase(@Valid @RequestBody PolicyDtos.PurchaseRequest request,
                                              Authentication authentication) {
        return policyService.purchase(authentication.getName(), request);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an active policy")
    public PolicyDtos.PolicyResponse cancel(@PathVariable Long id, Authentication authentication) {
        return policyService.cancel(authentication.getName(), id);
    }
}