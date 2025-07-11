package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.dto.AuthRequest;
import com.eaglebank.bankapi.repository.UserRepository;

import com.eaglebank.bankapi.security.CustomUserDetails;
import com.eaglebank.bankapi.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("Ping endpoint hit");
        return ResponseEntity.ok("pong");
    }

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Set<String> roles = userDetails.getRoles();

            String token = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);

            Map<Object, Object> response = new HashMap<>();
            response.put("email", userDetails.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}

