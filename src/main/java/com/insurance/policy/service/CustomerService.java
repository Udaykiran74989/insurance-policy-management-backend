package com.insurance.policy.service;

import com.insurance.policy.dto.UserDtos;

import java.util.List;

public interface CustomerService {
    UserDtos.UserResponse getProfile(String email);
    UserDtos.UserResponse updateProfile(String email, UserDtos.UpdateProfileRequest request);
    void changePassword(String email, UserDtos.ChangePasswordRequest request);
    List<UserDtos.UserResponse> findAllCustomers();
}