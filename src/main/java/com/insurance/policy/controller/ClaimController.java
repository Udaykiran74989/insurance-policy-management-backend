package com.insurance.policy.controller;

import com.insurance.policy.dto.ClaimDtos;
import com.insurance.policy.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Customer Claims")
@PreAuthorize("hasRole('CUSTOMER')")
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping
    @Operation(summary = "Submit a claim against an active customer policy")
    public ClaimDtos.ClaimResponse submit(@Valid @RequestBody ClaimDtos.ClaimRequest request,
                                           Authentication authentication) {
        return claimService.submit(authentication.getName(), request);
    }

    @GetMapping
    @Operation(summary = "List claims belonging to the authenticated customer")
    public List<ClaimDtos.ClaimResponse> list(Authentication authentication) {
        return claimService.findMine(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "View one of the authenticated customer's claims")
    public ClaimDtos.ClaimResponse get(@PathVariable Long id, Authentication authentication) {
        return claimService.findMineById(authentication.getName(), id);
    }
}