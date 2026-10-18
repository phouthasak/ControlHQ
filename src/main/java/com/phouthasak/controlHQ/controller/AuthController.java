package com.phouthasak.controlHQ.controller;

import com.phouthasak.controlHQ.domain.auth.AppUser;
import com.phouthasak.controlHQ.model.dto.BaseResponse;
import com.phouthasak.controlHQ.model.dto.auth.JwtToken;
import com.phouthasak.controlHQ.model.dto.auth.LoginDto;
import com.phouthasak.controlHQ.model.dto.auth.RegisterDto;
import com.phouthasak.controlHQ.service.auth.AuthService;
import com.phouthasak.controlHQ.service.auth.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<JwtToken>> login(@RequestBody LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        UserDetails user = userDetailsService.loadUserByUsername(loginDto.getUsername());
        JwtToken jwtToken = new JwtToken(jwtService.generateToken(user));
        return ResponseEntity.ok(BaseResponse.success(jwtToken));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterDto registerDto) {
        AppUser user = authService.register(registerDto);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", user.getId());
        responseMap.put("username", user.getUsername());
        responseMap.put("email", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(responseMap));
    }
}
