package com.eaglebank.bankapi.model;

import com.eaglebank.bankapi.dto.requests.CreateBankAccountRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bank_accounts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Pattern(regexp = "^01\\d{6}$", message = "Account number must start with 01 and be 8 digits long")
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @NotBlank
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{2}$", message = "Sort code must be in the format XX-XX-XX")
    @Column(name = "sort_code", nullable = false)
    private String sortCode = "10-10-10"; // Default sort code

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private CreateBankAccountRequest.AccountType accountType;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @DecimalMax(value = "10000.00", inclusive = true)
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotBlank
    @Pattern(regexp = "GBP", message = "Only GBP currency is supported")
    @Column(nullable = false)
    private String currency = "GBP";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdTimestamp;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedTimestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CreateBankAccountRequest.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(CreateBankAccountRequest.AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
}
