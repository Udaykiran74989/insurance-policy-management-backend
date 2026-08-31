package com.insurance.policy.controller;

import com.insurance.policy.dto.PremiumDtos;
import com.insurance.policy.service.PremiumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/premium")
@RequiredArgsConstructor
@Tag(name = "Premium Calculator")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Calculate an easy-to-explain indicative premium")
    public PremiumDtos.PremiumResponse calculate(@Valid @RequestBody PremiumDtos.PremiumRequest request) {
        return premiumService.calculate(request);
    }
}