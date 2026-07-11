package com.carlssonstudio.api.service;

import com.carlssonstudio.api.entity.AdminUser;
import com.carlssonstudio.api.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService
        implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        AdminUser user = adminUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Admin not found: " + username));

        return new org.springframework.security.core
                .userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isActive(),
            true, true, true,
            List.of(new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name()))
        );
    }
}