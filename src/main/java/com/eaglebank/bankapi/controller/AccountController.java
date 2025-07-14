package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.dto.AccountResponse;
import com.eaglebank.bankapi.model.Account;
import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepo;
    private final UserRepository userRepo;

    public AccountController(AccountRepository accountRepo, UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Account account, Authentication authentication) {
        List<String> missingFields = new ArrayList<>();

        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            missingFields.add("accountNumber");
        }

        if (account.getBalance() == null) {
            missingFields.add("balance");
        }

        if (!missingFields.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Missing required field(s): " + String.join(", ", missingFields));
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user token");
        }

        User authenticatedUser = userOpt.get();

        if (!authenticatedUser.getId().equals(account.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User mismatch");
        }

        account.setUser(authenticatedUser);
        Account saved = accountRepo.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", saved.getId(), "accountNumber", saved.getAccountNumber()));
    }


    @GetMapping
    public ResponseEntity<?> getAllAccountsForAuthenticatedUser(Authentication authentication) {
        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        List<Account> accounts = accountRepo.findByUserId(userOpt.get().getId());

        List<AccountResponse> response = accounts.stream()
                .map(a -> new AccountResponse(
                        a.getId(),
                        a.getAccountNumber(),
                        a.getBalance(),
                        a.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable UUID id, Authentication authentication) {
        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        User loggedInUser = userOpt.get();

        Optional<Account> accountOpt = accountRepo.findById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUser().getId().equals(loggedInUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!accountRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        accountRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
