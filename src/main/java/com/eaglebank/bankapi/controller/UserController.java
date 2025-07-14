package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import com.eaglebank.bankapi.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepo, AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userRepo.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id, Authentication authentication) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();

        // Now get the logged-in user by email from authentication
        String loggedInEmail = authentication.getName();
        Optional<User> loggedInUserOpt = userRepo.findByEmail(loggedInEmail);
        if (loggedInUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }
        User loggedInUser = loggedInUserOpt.get();

        // Check if the logged-in user is allowed to access the requested user's info
        if (!loggedInUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Return the user if all checks pass
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User newUser) {
        return userRepo.findById(id).map(user -> {
            user.setName(newUser.getName());
            return userRepo.save(user);
        }).orElseGet(() -> {
            newUser.setId(id);
            return userRepo.save(newUser);
        });
    }

    @PostMapping("/v1/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one role must be provided");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        // Hash the raw password before saving
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        User savedUser = userRepo.save(user);
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

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUser(@PathVariable UUID id,
                                       @RequestBody Map<String, Object> updates,
                                       Authentication authentication) {
        Optional<User> authUserOpt = userRepo.findByEmail(authentication.getName());
        if (authUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        User authUser = authUserOpt.get();

        Optional<User> targetUserOpt = userRepo.findById(id);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User targetUser = targetUserOpt.get();

        if (!authUser.getId().equals(targetUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (updates.containsKey("name")) {
            targetUser.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            targetUser.setEmail((String) updates.get("email"));
        }

        userRepo.save(targetUser);

        var response = Map.of(
                "id", targetUser.getId(),
                "name", targetUser.getName(),
                "email", targetUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, Authentication authentication) {
        Optional<User> authUserOpt = userRepo.findByEmail(authentication.getName());
        if (authUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        User authUser = authUserOpt.get();

        Optional<User> targetUserOpt = userRepo.findById(id);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User targetUser = targetUserOpt.get();

        if (!authUser.getId().equals(targetUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        boolean hasAccounts = accountRepo.existsByUserId(id);
        if (hasAccounts) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has existing accounts");
        }

        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
