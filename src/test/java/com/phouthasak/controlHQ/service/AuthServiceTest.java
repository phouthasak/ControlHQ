package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.domain.auth.AppUser;
import com.phouthasak.controlHQ.exception.InvalidException;
import com.phouthasak.controlHQ.model.dto.auth.RegisterDto;
import com.phouthasak.controlHQ.repository.UserRepository;
import com.phouthasak.controlHQ.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({AuthService.class, AuthServiceTest.Config.class})
class AuthServiceTest {
    @TestConfiguration
    static class Config {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_savesUserWithHashedPasswordAndDefaultRole() {
        AppUser user = authService.register(new RegisterDto("alice", "alice@example.com", "password123"));

        AppUser saved = userRepository.findByUsername("alice").orElseThrow();
        assertEquals(user.getId(), saved.getId());
        assertNotEquals("password123", saved.getPassword());
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
        assertTrue(saved.isEnabled());
        assertEquals(1, saved.getRoles().size());
        assertEquals("USER", saved.getRoles().iterator().next().getName());
    }

    @Test
    void register_reusesExistingRole() {
        authService.register(new RegisterDto("alice", "alice@example.com", "password123"));
        AppUser second = authService.register(new RegisterDto("bob", "bob@example.com", "password123"));

        assertEquals(1, second.getRoles().size());
        assertEquals(
                userRepository.findByUsername("alice").orElseThrow().getRoles().iterator().next().getId(),
                second.getRoles().iterator().next().getId());
    }

    @Test
    void register_duplicateUsernameThrows() {
        authService.register(new RegisterDto("alice", "alice@example.com", "password123"));

        assertThrows(InvalidException.class,
                () -> authService.register(new RegisterDto("alice", "other@example.com", "password123")));
    }

    @Test
    void register_duplicateEmailThrows() {
        authService.register(new RegisterDto("alice", "alice@example.com", "password123"));

        assertThrows(InvalidException.class,
                () -> authService.register(new RegisterDto("bob", "alice@example.com", "password123")));
    }
}
