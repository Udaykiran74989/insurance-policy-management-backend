package com.insurance.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserDtos {
    private UserDtos() {}

    public record UserResponse(Long id, String name, String email, String phone, String address,
                               String role, String status) {}

    public record UpdateProfileRequest(
            @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters")
            String name,
            @Size(max = 20, message = "Phone must be at most 20 characters") String phone,
            @Size(max = 255, message = "Address must be at most 255 characters") String address) {}

    public record ChangePasswordRequest(
            @NotBlank(message = "Current password is required") String currentPassword,
            @NotBlank(message = "New password is required")
            @Size(min = 8, max = 100, message = "New password must contain 8 to 100 characters") String newPassword) {}
}