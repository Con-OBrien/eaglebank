package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.dto.requests.AddressRequest;
import com.eaglebank.bankapi.dto.requests.CreateUserRequest;
import com.eaglebank.bankapi.dto.requests.UpdateUserRequest;
import com.eaglebank.bankapi.dto.responses.AddressResponse;
import com.eaglebank.bankapi.dto.responses.UserResponse;
import com.eaglebank.bankapi.model.Address;
import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import com.eaglebank.bankapi.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    private Address convertToAddressEntity(AddressRequest requestAddress) {
        Address address = new Address();
        address.setLine1(requestAddress.getLine1());
        address.setLine2(requestAddress.getLine2());
        address.setLine3(requestAddress.getLine3());
        address.setTown(requestAddress.getTown());
        address.setCounty(requestAddress.getCounty());
        address.setPostcode(requestAddress.getPostcode());
        return address;
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setLine1(user.getAddress().getLine1());
        addressResponse.setLine2(user.getAddress().getLine2());
        addressResponse.setLine3(user.getAddress().getLine3());
        addressResponse.setTown(user.getAddress().getTown());
        addressResponse.setCounty(user.getAddress().getCounty());
        addressResponse.setPostcode(user.getAddress().getPostcode());

        response.setAddress(addressResponse);
        response.setCreatedTimestamp(user.getCreatedAt().atOffset(ZoneOffset.UTC));
        response.setUpdatedTimestamp(user.getUpdatedAt().atOffset(ZoneOffset.UTC));

        return response;
    }

    public UserController(UserRepository userRepo, AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(convertToAddressEntity(request.getAddress()));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String testPassword = "testPassword"; // This should be replaced with a proper password handling logic
        user.setPassword(encoder.encode(testPassword));

        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepo.save(user);

        UserResponse response = convertToUserResponse(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id, Authentication authentication) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();

        String loggedInEmail = authentication.getName();
        Optional<User> loggedInUserOpt = userRepo.findByEmail(loggedInEmail);
        if (loggedInUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }
        User loggedInUser = loggedInUserOpt.get();

        if (!loggedInUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id,
                                        @Valid @RequestBody UpdateUserRequest request) {

        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setUpdatedAt(Instant.now());

        User saved = userRepo.save(user);

        return ResponseEntity.ok(convertToUserResponse(saved));
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
