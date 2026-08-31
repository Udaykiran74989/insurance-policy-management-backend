package com.insurance.policy.service.impl;

import com.insurance.policy.dto.AuthDtos;
import com.insurance.policy.entity.User;
import com.insurance.policy.exception.BadRequestException;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.security.JwtService;
import com.insurance.policy.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("An account already exists for this email");
        }
        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .address(request.address())
                .role(User.Role.CUSTOMER)
                .status(User.UserStatus.ACTIVE)
                .build();
        User saved = userRepository.save(user);
        log.info("Registered customer account for {}", saved.getEmail());
        return toResponse(saved, jwtService.generateToken(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("User account was not found"));
        log.info("Successful login for {}", user.getEmail());
        return toResponse(user, jwtService.generateToken(user));
    }

    private AuthDtos.AuthResponse toResponse(User user, String token) {
        return new AuthDtos.AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), token);
    }
}