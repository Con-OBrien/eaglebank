package com.eaglebank.bankapi.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private BigDecimal balance;
    private Instant createdAt;

    public AccountResponse(UUID id, String accountNumber, BigDecimal balance, Instant createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    // Getters only
    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
