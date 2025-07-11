package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.UserRepository;
import com.eaglebank.bankapi.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> all() {
        return repo.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repo.save(user);
    }

    @GetMapping("/{id}")
    public Optional<User> get(@PathVariable UUID id) {
        return repo.findById(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User newUser) {
        return repo.findById(id).map(user -> {
            user.setName(newUser.getName());
            return repo.save(user);
        }).orElseGet(() -> {
            newUser.setId(id);
            return repo.save(newUser);
        });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repo.deleteById(id);
    }

    @PostMapping("/v1/users")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        // Validate required fields: use Bean Validation annotations (@NotNull, @Email, etc) in your User model

        if (repo.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User savedUser = repo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "email", userDetails.getUsername(),
                "roles", userDetails.getRoles()
        ));
    }


}
