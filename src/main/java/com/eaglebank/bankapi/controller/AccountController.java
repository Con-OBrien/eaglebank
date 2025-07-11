package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.model.Account;
import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepo;
    private final UserRepository userRepo;

    public AccountController(AccountRepository accountRepo, UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public List<Account> all() {
        return accountRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Account account) {
        if (account.getUser() == null || account.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required in the user object");
        }

        Optional<User> userOpt = userRepo.findById(account.getUser().getId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user ID");
        }

        account.setUser(userOpt.get());
        Account saved = accountRepo.save(account);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable UUID id) {
        return accountRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
