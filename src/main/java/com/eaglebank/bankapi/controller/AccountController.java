package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.dto.AccountResponse;
import com.eaglebank.bankapi.dto.TransactionRequest;
import com.eaglebank.bankapi.model.Account;
import com.eaglebank.bankapi.model.Transaction;
import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.TransactionRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;

    public AccountController(AccountRepository accountRepo, UserRepository userRepo, TransactionRepository transactionRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
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

    @PostMapping("/{id}/transactions")
    public ResponseEntity<?> handleTransaction(@PathVariable UUID id,
                                               @RequestBody TransactionRequest request,
                                               Authentication authentication) {
        // Validate type
        if (request.getType() == null || (!request.getType().equalsIgnoreCase("DEPOSIT") && !request.getType().equalsIgnoreCase("WITHDRAW"))) {
            return ResponseEntity.badRequest().body("Invalid transaction type. Must be 'DEPOSIT' or 'WITHDRAW'");
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be a positive number");
        }

        // Authenticate user
        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        // Find account
        Optional<Account> accountOpt = accountRepo.findById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();

        // Check ownership
        if (!account.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (request.getType().equalsIgnoreCase("WITHDRAW") && account.getBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.unprocessableEntity().body("Insufficient funds");
        }

        BigDecimal newBalance = request.getType().equalsIgnoreCase("DEPOSIT")
                ? account.getBalance().add(request.getAmount())
                : account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);
        accountRepo.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(request.getType().toUpperCase());
        transaction.setDescription(request.getDescription());

        transactionRepo.save(transaction);

        var response = Map.of(
                "transactionId", transaction.getId(),
                "accountId", account.getId(),
                "newBalance", account.getBalance(),
                "transactionType", transaction.getTransactionType(),
                "timestamp", transaction.getTransactionDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactionsForAccount(@PathVariable UUID accountId, Authentication authentication) {
        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        Optional<Account> accountOpt = accountRepo.findById(accountId);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();
        if (!account.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Transaction> transactions = transactionRepo.findByAccountId(accountId);

        return ResponseEntity.ok(
                transactions.stream().map(tx -> Map.of(
                        "id", tx.getId(),
                        "amount", tx.getAmount(),
                        "type", tx.getTransactionType(),
                        "description", tx.getDescription(),
                        "transactionDate", tx.getTransactionDate()
                )).toList()
        );
    }

    @GetMapping("/{accountId}/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable UUID accountId,
                                                @PathVariable UUID transactionId,
                                                Authentication authentication) {
        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        Optional<Account> accountOpt = accountRepo.findById(accountId);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();
        if (!account.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Optional<Transaction> txOpt = transactionRepo.findById(transactionId);
        if (txOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }

        Transaction tx = txOpt.get();
        if (!tx.getAccount().getId().equals(accountId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction does not belong to this account");
        }

        var response = Map.of(
                "id", tx.getId(),
                "amount", tx.getAmount(),
                "type", tx.getTransactionType(),
                "description", tx.getDescription(),
                "transactionDate", tx.getTransactionDate()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchAccount(@PathVariable UUID id,
                                          @RequestBody Map<String, Object> updates,
                                          Authentication authentication) {

        Optional<User> authUserOpt = userRepo.findByEmail(authentication.getName());
        if (authUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        User authUser = authUserOpt.get();

        Optional<Account> accountOpt = accountRepo.findById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();

        if (!account.getUser().getId().equals(authUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (updates.containsKey("accountNumber")) {
            account.setAccountNumber((String) updates.get("accountNumber"));
        }

        accountRepo.save(account);

        var response = Map.of(
                "id", account.getId(),
                "accountNumber", account.getAccountNumber(),
                "balance", account.getBalance(),
                "createdAt", account.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }


}
