package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.JwtUtil;
import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.AdminUser;
import com.carlssonstudio.api.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException(
                "Invalid username or password");
        }

        AdminUser user = adminUserRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                    new RuntimeException("User not found"));

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(user);

        String token = jwtUtil.generateToken(
            user.getUsername(),
            user.getRole().name()
        );

        log.info("Admin login: username={}",
            user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}