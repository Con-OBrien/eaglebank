package com.eaglebank.bankapi.controller;

import com.eaglebank.bankapi.model.Account;
import com.eaglebank.bankapi.model.Transaction;
import com.eaglebank.bankapi.repository.AccountRepository;
import com.eaglebank.bankapi.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    public TransactionController(TransactionRepository transactionRepo, AccountRepository accountRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }

    @GetMapping
    public List<Transaction> all() {
        return transactionRepo.findAll();
    }

    @PostMapping
    public Transaction create(@RequestBody Transaction tx) {
        // Ensure account exists before associating
        UUID accountId = tx.getAccount().getId();
        Optional<Account> account = accountRepo.findById(accountId);
        account.ifPresent(tx::setAccount);

        return transactionRepo.save(tx);
    }

    @GetMapping("/{id}")
    public Transaction get(@PathVariable UUID id) {
        return transactionRepo.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        transactionRepo.deleteById(id);
    }
}
