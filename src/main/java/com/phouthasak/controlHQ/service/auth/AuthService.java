package com.phouthasak.controlHQ.service.auth;

import com.phouthasak.controlHQ.domain.auth.AppUser;
import com.phouthasak.controlHQ.domain.auth.Role;
import com.phouthasak.controlHQ.exception.InvalidException;
import com.phouthasak.controlHQ.model.dto.auth.RegisterDto;
import com.phouthasak.controlHQ.repository.RoleRepository;
import com.phouthasak.controlHQ.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    private static final String DEFAULT_ROLE = "USER";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new InvalidException("username_already_exists");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new InvalidException("email_already_exists");
        }

        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseGet(() -> roleRepository.save(Role.builder().name(DEFAULT_ROLE).build()));

        return userRepository.save(AppUser.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(Set.of(role))
                .build());
    }
}
