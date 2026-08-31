package com.insurance.policy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters")
            String name,
            @NotBlank(message = "Email is required") @Email(message = "Email must be valid")
            String email,
            @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must contain 8 to 100 characters")
            String password,
            @Size(max = 20, message = "Phone must be at most 20 characters") String phone,
            @Size(max = 255, message = "Address must be at most 255 characters") String address) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
            @NotBlank(message = "Password is required") String password) {}

    public record AuthResponse(Long userId, String name, String email, String role, String token) {}
}