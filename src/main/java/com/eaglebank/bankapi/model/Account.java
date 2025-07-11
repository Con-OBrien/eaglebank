package com.eaglebank.bankapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "bank_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "account_number")
})
public class Account {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Account() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // No setter for createdAt because it's set once on creation
}
