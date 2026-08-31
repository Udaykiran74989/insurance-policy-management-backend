package com.insurance.policy.service.impl;

import com.insurance.policy.dto.UserDtos;
import com.insurance.policy.entity.User;
import com.insurance.policy.exception.BadRequestException;
import com.insurance.policy.exception.ResourceNotFoundException;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserResponse getProfile(String email) {
        return toResponse(findUser(email));
    }

    @Override
    @Transactional
    public UserDtos.UserResponse updateProfile(String email, UserDtos.UpdateProfileRequest request) {
        User user = findUser(email);
        user.setName(request.name().trim());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String email, UserDtos.ChangePasswordRequest request) {
        User user = findUser(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtos.UserResponse> findAllCustomers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(User.Role.CUSTOMER)
                .stream().map(this::toResponse).toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }

    private UserDtos.UserResponse toResponse(User user) {
        return new UserDtos.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.getRole().name(), user.getStatus().name());
    }
}