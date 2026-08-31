package com.insurance.policy.controller;

import com.insurance.policy.dto.UserDtos;
import com.insurance.policy.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/profile")
    @Operation(summary = "View the authenticated customer's profile")
    public UserDtos.UserResponse profile(Authentication authentication) {
        return customerService.getProfile(authentication.getName());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update the authenticated customer's profile")
    public UserDtos.UserResponse update(@Valid @RequestBody UserDtos.UpdateProfileRequest request,
                                        Authentication authentication) {
        return customerService.updateProfile(authentication.getName(), request);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change the authenticated customer's password")
    public void changePassword(@Valid @RequestBody UserDtos.ChangePasswordRequest request,
                               Authentication authentication) {
        customerService.changePassword(authentication.getName(), request);
    }
}