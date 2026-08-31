package com.insurance.policy.service;

import com.insurance.policy.dto.AuthDtos;
import com.insurance.policy.entity.User;
import com.insurance.policy.repository.UserRepository;
import com.insurance.policy.security.JwtService;
import com.insurance.policy.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @InjectMocks AuthServiceImpl authService;

    @Test
    void loginAuthenticatesUserAndReturnsJwt() {
        User user = User.builder().id(1L).name("Anita").email("anita@example.com")
                .password("encoded").role(User.Role.CUSTOMER).status(User.UserStatus.ACTIVE).build();
        when(userRepository.findByEmailIgnoreCase("anita@example.com")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthDtos.AuthResponse response = authService.login(
                new AuthDtos.LoginRequest("anita@example.com", "Customer@123"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.role()).isEqualTo("CUSTOMER");
        verify(authenticationManager).authenticate(any());
    }
}