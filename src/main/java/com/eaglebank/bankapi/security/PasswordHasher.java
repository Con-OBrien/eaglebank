package com.eaglebank.bankapi.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Just simplify for the purposes of this assignment
public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";  // <-- change this to what you want
        String hashed = encoder.encode(rawPassword);
        System.out.println(hashed);
    }
}
