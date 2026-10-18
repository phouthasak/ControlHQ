package com.phouthasak.controlHQ.service;

import com.phouthasak.controlHQ.domain.auth.AppUser;
import com.phouthasak.controlHQ.domain.auth.Role;
import com.phouthasak.controlHQ.repository.RoleRepository;
import com.phouthasak.controlHQ.repository.UserRepository;
import com.phouthasak.controlHQ.service.auth.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(CustomUserDetailsService.class)
class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void loadUserByUsername_mapsPasswordAndRoleAuthorities() {
        Role role = roleRepository.save(Role.builder().name("USER").build());
        userRepository.save(AppUser.builder()
                .username("alice")
                .email("alice@example.com")
                .password("hashed")
                .roles(Set.of(role))
                .build());

        UserDetails details = customUserDetailsService.loadUserByUsername("alice");

        assertEquals("alice", details.getUsername());
        assertEquals("hashed", details.getPassword());
        assertTrue(details.isEnabled());
        assertEquals(Set.of("ROLE_USER"),
                Set.copyOf(details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()));
    }

    @Test
    void loadUserByUsername_disabledUserIsNotEnabled() {
        userRepository.save(AppUser.builder()
                .username("bob")
                .email("bob@example.com")
                .password("hashed")
                .enabled(false)
                .build());

        assertFalse(customUserDetailsService.loadUserByUsername("bob").isEnabled());
    }

    @Test
    void loadUserByUsername_unknownUserThrows() {
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nobody"));
    }
}
