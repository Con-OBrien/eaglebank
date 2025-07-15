package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.dto.AccountResponse;
import com.eaglebank.bankapi.dto.TransactionRequest;
import com.eaglebank.bankapi.dto.requests.CreateBankAccountRequest;
import com.eaglebank.bankapi.dto.responses.BankAccountResponse;
import com.eaglebank.bankapi.dto.responses.TransactionResponse;
import com.eaglebank.bankapi.model.Account;
import com.eaglebank.bankapi.model.Transaction;
import com.eaglebank.bankapi.model.User;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.TransactionRepository;
import com.eaglebank.bankapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
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

    private String generateAccountNumber() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // 6-digit number
        return "01" + randomNumber;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> create(
            @Valid @RequestBody CreateBankAccountRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user"));

        Account account = new Account();
        account.setUser(user);
        account.setName(request.getName());
        account.setAccountType(request.getAccountType());

        account.setAccountNumber(generateAccountNumber());
        account.setSortCode("10-10-10");
        account.setCurrency("GBP");
        account.setBalance(BigDecimal.ZERO);

        Instant now = Instant.now();
        account.setCreatedTimestamp(now);
        account.setUpdatedTimestamp(now);

        Account saved = accountRepo.save(account);

        BankAccountResponse response = getBankAccountResponse(saved);


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private static BankAccountResponse getBankAccountResponse(Account saved) {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber(saved.getAccountNumber());
        response.setSortCode(saved.getSortCode());
        response.setName(saved.getName());
        response.setAccountType(saved.getAccountType());
        response.setBalance(saved.getBalance());
        response.setCurrency(saved.getCurrency());
        response.setCreatedTimestamp(saved.getCreatedTimestamp());
        response.setUpdatedTimestamp(saved.getUpdatedTimestamp());
        return response;
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
                        a.getCreatedTimestamp()
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
                account.getCreatedTimestamp()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/transactions")
    public ResponseEntity<?> handleTransaction(@PathVariable UUID id,
                                               @RequestBody TransactionRequest request,
                                               Authentication authentication) {
        if (request.getType() == null || (!request.getType().equalsIgnoreCase("DEPOSIT") && !request.getType().equalsIgnoreCase("WITHDRAW"))) {
            return ResponseEntity.badRequest().body("Invalid transaction type. Must be 'DEPOSIT' or 'WITHDRAW'");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Amount must be a positive number");
        }

        Optional<User> userOpt = userRepo.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        Optional<Account> accountOpt = accountRepo.findById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();

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

        // Map to TransactionResponse
        TransactionResponse response = new TransactionResponse();
        response.setId("tan-" + tx.getId().toString().replace("-", ""));
        response.setAmount(tx.getAmount().doubleValue());
        response.setType(tx.getTransactionType());
        response.setReference(tx.getDescription());
        response.setUserId("usr-" + tx.getAccount().getUser().getId().toString().replace("-", ""));
        response.setCreatedTimestamp(tx.getTransactionDate().atOffset(ZoneOffset.UTC)); // if LocalDateTime

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

        if (updates.containsKey("name")) {
            String name = updates.get("name").toString().trim();
            if (!name.isEmpty()) {
                account.setName(name);
            }
        }

        if (updates.containsKey("accountType")) {
            try {
                String typeStr = updates.get("accountType").toString().toUpperCase();
                CreateBankAccountRequest.AccountType type = CreateBankAccountRequest.AccountType.valueOf(typeStr); // Validate enum
                account.setAccountType(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid accountType. Only 'PERSONAL' is supported.");
            }
        }

        if (updates.containsKey("accountNumber")) {
            String accountNumber = updates.get("accountNumber").toString().trim();
            if (!accountNumber.matches("^01\\d{6}$")) {
                return ResponseEntity.badRequest().body("Invalid accountNumber format. Expected format: ^01\\d{6}$");
            }
            account.setAccountNumber(accountNumber);
        }

        account.setUpdatedTimestamp(Instant.now());
        accountRepo.save(account);

        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setSortCode(account.getSortCode());
        response.setName(account.getName());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setCreatedTimestamp(account.getCreatedTimestamp());
        response.setUpdatedTimestamp(account.getUpdatedTimestamp());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable UUID id, Authentication authentication) {
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

        accountRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
