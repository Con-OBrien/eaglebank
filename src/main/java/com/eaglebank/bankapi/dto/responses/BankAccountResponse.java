package com.eaglebank.bankapi.dto.responses;

import com.eaglebank.bankapi.dto.requests.CreateBankAccountRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing bank account details")
public class BankAccountResponse {

    @Schema(
            description = "8-digit account number starting with 01",
            example = "01234567",
            pattern = "^01\\d{6}$"
    )
    private String accountNumber;

    @Schema(
            description = "Standard sort code for the bank",
            example = "10-10-10",
            allowableValues = {"10-10-10"}
    )
    private String sortCode;

    @Schema(
            description = "Friendly name for the bank account",
            example = "My Personal Account"
    )
    private String name;

    @Schema(
            description = "Type of the bank account",
            example = "personal",
            allowableValues = {"personal"}
    )
    private CreateBankAccountRequest.AccountType accountType;

    @Schema(
            description = "Current balance of the bank account (0.00 to 10000.00)",
            example = "1000.00",
            minimum = "0.00",
            maximum = "10000.00"
    )
    private BigDecimal balance;

    @Schema(
            description = "Currency of the account (GBP only supported)",
            example = "GBP",
            allowableValues = {"GBP"}
    )
    private String currency;

    @Schema(
            description = "Timestamp when the account was created",
            example = "2025-07-14T10:15:30Z"
    )
    private Instant createdTimestamp;

    @Schema(
            description = "Timestamp when the account was last updated",
            example = "2025-07-14T12:00:00Z"
    )
    private Instant updatedTimestamp;

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
